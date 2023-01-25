#include "sql.h"
#include "utils/string.h"

#include <inttypes.h>
#include <pthread.h>
#include <stddef.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

#define QUERY_SMALL 512
#define QUERY_LARGE BUFSIZ

#define ROLLBACK()                                        \
	{                                                       \
		*lastrc = rc;                                         \
		rc = sqlite3_exec(db, "ROLLBACK;", NULL, NULL, NULL); \
	}

inline int sql_is_ok(int rc) { return (rc == SQLITE_OK) || (rc == SQLITE_ROW) || (rc == SQLITE_DONE) || (rc == SQLITE_EMPTY); }
inline int sql_is_err(int rc) { return !sql_is_ok(rc); }

static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

static uint32_t sql_insert(sqlite3 *db, const char *query, int *lastrc)
{
	/* printf("Insert query: %s\n", query); */
	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	uint32_t newid;

	pthread_mutex_lock(&mutex);
	if ((rc = sqlite3_step(stmt)) != SQLITE_DONE)
	{
		*lastrc = rc;
		pthread_mutex_unlock(&mutex);
		sqlite3_finalize(stmt);
		return 0;
	}
	newid = sqlite3_last_insert_rowid(db);
	pthread_mutex_unlock(&mutex);

	sqlite3_finalize(stmt);
	*lastrc = SQLITE_OK;
	return newid;
}

static sllnode_t *sql_get_list(sqlite3 *db, const char *query, int *lastrc)
{
	sllnode_t *list = NULL;

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return NULL;
	}

	while ((rc = sqlite3_step(stmt)) == SQLITE_ROW)
		sll_insert_node(&list, new_jval_l(sqlite3_column_int(stmt, 0)));

	if (rc != SQLITE_DONE)
		*lastrc = rc;

	sqlite3_finalize(stmt);
	*lastrc = SQLITE_OK;
	return list;
}

//////////////////////////////////////////////////

user_schema *user_get_by_id(sqlite3 *db, uint32_t id, int *lastrc)
{
	user_schema *res = (user_schema *)calloc(1, sizeof(user_schema));
	if (!res)
	{
		*lastrc = SQLITE_NOMEM;
		return NULL;
	}

	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT * FROM users WHERE id=%" PRIu32 "", id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		free(res);
		return NULL;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		free(res);
		return NULL;
	}

	res->id = id;
	res->uname = string_new(sqlite3_column_text(stmt, 1));
	res->password = string_new(sqlite3_column_text(stmt, 2));
	res->phone = string_new(sqlite3_column_text(stmt, 3));
	res->email = string_new(sqlite3_column_text(stmt, 4));

	sqlite3_finalize(stmt);
	*lastrc = SQLITE_OK;
	return res;
}

user_schema *user_get_by_uname(sqlite3 *db, const char *uname, int *lastrc)
{
	user_schema *res = (user_schema *)calloc(1, sizeof(user_schema));
	if (!res)
	{
		*lastrc = SQLITE_NOMEM;
		return NULL;
	}

	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id, uname, password FROM users WHERE uname='%s'", uname);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return NULL;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		return NULL;
	}

	res->id = sqlite3_column_int(stmt, 0);
	res->uname = string_new(uname);
	res->password = string_new(sqlite3_column_text(stmt, 2));

	sqlite3_finalize(stmt);
	*lastrc = SQLITE_OK;
	return res;
}

sllnode_t *user_search_by_uname(sqlite3 *db, const char *uname, int32_t limit, int32_t offset, int *lastrc)
{
	sllnode_t *list = NULL;

	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id FROM users WHERE uname LIKE '%%%s%%' LIMIT %" PRId32 " OFFSET %" PRId32 "", uname, limit, offset);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return NULL;
	}

	while ((rc = sqlite3_step(stmt)) == SQLITE_ROW)
		sll_insert_node(&list, new_jval_l(sqlite3_column_int(stmt, 0)));

	if (rc != SQLITE_DONE)
		*lastrc = rc;

	sqlite3_finalize(stmt);

	*lastrc = SQLITE_OK;
	return list;
}

uint32_t user_create(sqlite3 *db, const user_schema *info, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL,
					 "INSERT INTO users(uname, password, phone, email) VALUES ('%s', '%s', '%s', '%s');",
					 info->uname, info->password, info->phone, info->email);

	return sql_insert(db, query, lastrc);
}

void user_drop(sqlite3 *db, uint32_t id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "DELETE FROM users WHERE id=%" PRIu32 ";", id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_DONE)
	{
		*lastrc = rc;
		sqlite3_finalize(stmt);
		return;
	}

	sqlite3_finalize(stmt);
	*lastrc = SQLITE_OK;
}

sllnode_t *user_get_conv_list(sqlite3 *db, uint32_t user_id, int32_t limit, int32_t offset, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT conv_id FROM members WHERE user_id=%" PRIu32 " ORDER BY rowid DESC LIMIT %" PRId32 " OFFSET %" PRId32 "", user_id, limit, offset);

	return sql_get_list(db, query, lastrc);
}

sllnode_t *user_get_chat_list(sqlite3 *db, uint32_t user_id, int32_t limit, int32_t offset, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id FROM chats WHERE member1=%" PRIu32 " OR member2=%" PRIu32 " ORDER BY id DESC LIMIT %" PRId32 " OFFSET %" PRId32 "", user_id, user_id, limit, offset);

	return sql_get_list(db, query, lastrc);
}

void user_free(user_schema *user)
{
	if (!user)
		return;
	string_remove(user->uname);
	string_remove(user->password);
	string_remove(user->phone);
	string_remove(user->email);
	free(user);
}

//////////////////////////////////////////////////

uint32_t conv_create(sqlite3 *db, uint32_t admin_id, const char *name, int *lastrc)
{
	char query[QUERY_SMALL];

	int rc = sqlite3_exec(db, "PRAGMA foreign_keys = ON;BEGIN TRANSACTION;", NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "INSERT INTO conversations(admin_id,name) VALUES(%" PRIu32 ", '%s');", admin_id, name);

	pthread_mutex_lock(&mutex);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	uint32_t newid = sqlite3_last_insert_rowid(db);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return 0;
	}
	pthread_mutex_unlock(&mutex);

	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "INSERT INTO members(conv_id, user_id) VALUES(last_insert_rowid(), %" PRIu32 ");", admin_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return 0;
	}

	rc = sqlite3_exec(db, "COMMIT TRANSACTION;", NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return 0;
	}

	*lastrc = SQLITE_OK;
	return newid;
}

int conv_is_admin(sqlite3 *db, uint32_t conv_id, uint32_t user_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id FROM conversations WHERE id=%" PRIu32 " AND admin_id=%" PRIu32 "", conv_id, user_id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		return 0;
	}

	sqlite3_finalize(stmt);

	*lastrc = SQLITE_OK;
	return 1;
}

int conv_is_member(sqlite3 *db, uint32_t conv_id, uint32_t user_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT rowid FROM members WHERE conv_id=%" PRIu32 " AND user_id=%" PRIu32 "", conv_id, user_id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		return 0;
	}

	sqlite3_finalize(stmt);

	*lastrc = SQLITE_OK;
	return 1;
}

void conv_drop(sqlite3 *db, uint32_t conv_id, int *lastrc)
{
	int rc;
	char query[QUERY_SMALL];

	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "BEGIN TRANSACTION;");
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "DELETE FROM messages WHERE conv_id=%" PRIu32 ";", conv_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "DELETE FROM members WHERE conv_id=%" PRIu32 ";", conv_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "DELETE FROM conversations WHERE id=%" PRIu32 ";", conv_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "COMMIT TRANSACTION;");
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	*lastrc = SQLITE_OK;
}

void conv_join(sqlite3 *db, uint32_t user_id, uint32_t conv_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "INSERT INTO members(conv_id, user_id) VALUES (%" PRIu32 ", %" PRIu32 ");", conv_id, user_id);

	sql_insert(db, query, lastrc);
}

void conv_quit(sqlite3 *db, uint32_t user_id, uint32_t conv_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL,
					 "DELETE FROM members WHERE conv_id=%" PRIu32 " AND user_id=%" PRIu32 "",
					 conv_id, user_id);

	int rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return;
	}
	*lastrc = SQLITE_OK;
}

conv_schema *conv_get_info(sqlite3 *db, uint32_t conv_id, int *lastrc)
{
	conv_schema *res = (conv_schema *)calloc(1, sizeof(conv_schema));
	if (!res)
	{
		*lastrc = SQLITE_NOMEM;
		return NULL;
	}

	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT * FROM conversations WHERE id=%" PRIu32 "", conv_id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		free(res);
		return NULL;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		free(res);
		return NULL;
	}

	res->id = conv_id;
	res->admin_id = sqlite3_column_int(stmt, 1);
	res->name = string_new(sqlite3_column_text(stmt, 2));

	sqlite3_finalize(stmt);

	*lastrc = SQLITE_OK;
	return res;
}

sllnode_t *conv_get_members(sqlite3 *db, uint32_t conv_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT user_id from members WHERE conv_id=%" PRIu32 "", conv_id);

	return sql_get_list(db, query, lastrc);
}

void conv_free(conv_schema *conv)
{
	if (!conv)
		return;
	string_remove(conv->name);
	free(conv);
}

//////////////////////////////////////////////////

uint32_t chat_create(sqlite3 *db, uint32_t member1, uint32_t member2, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "PRAGMA foreign_keys=ON;");
	int rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "INSERT INTO chats(member1, member2) VALUES (%" PRIu32 ", %" PRIu32 ");", member1, member2);

	return sql_insert(db, query, lastrc);
}

int chat_is_member(sqlite3 *db, uint32_t chat_id, uint32_t user_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id FROM chats WHERE id=%" PRIu32 " AND (member1=%" PRIu32 " OR member2=%" PRIu32 ")", chat_id, user_id, user_id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		return 0;
	}

	sqlite3_finalize(stmt);
	*lastrc = SQLITE_OK;

	return 1;
}

void chat_drop(sqlite3 *db, uint32_t chat_id, int *lastrc)
{
	int rc;
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "BEGIN TRANSACTION;");
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "DELETE FROM messages WHERE chat_id=%" PRIu32 ";", chat_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "DELETE FROM chats WHERE id=%" PRIu32 ";", chat_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	snprintf(query, QUERY_SMALL, "COMMIT TRANSACTION;", chat_id);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		ROLLBACK();
		return;
	}

	*lastrc = SQLITE_OK;
}

chat_schema *chat_get_info(sqlite3 *db, uint32_t chat_id, int *lastrc)
{
	chat_schema *res = (chat_schema *)calloc(1, sizeof(chat_schema));
	if (!res)
	{
		*lastrc = SQLITE_NOMEM;
		return NULL;
	}

	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT * FROM chats WHERE id=%" PRIu32 "", chat_id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		free(res);
		return NULL;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		free(res);
		return NULL;
	}

	res->id = chat_id;
	res->member1 = sqlite3_column_int(stmt, 1);
	res->member2 = sqlite3_column_int(stmt, 2);

	sqlite3_finalize(stmt);

	*lastrc = SQLITE_OK;
	return res;
}

void chat_free(chat_schema *chat)
{
	if (!chat)
		return;
	free(chat);
}

//////////////////////////////////////////////////

sllnode_t *msg_conv_get_all(sqlite3 *db, uint32_t conv_id, int32_t limit, int32_t offset, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id FROM messages WHERE conv_id=%" PRIu32 " ORDER BY created_at DESC LIMIT %" PRId32 " OFFSET %" PRId32 "", conv_id, limit, offset);

	return sql_get_list(db, query, lastrc);
}

sllnode_t *msg_chat_get_all(sqlite3 *db, uint32_t chat_id, int32_t limit, int32_t offset, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT id FROM messages WHERE chat_id=%" PRIu32 " ORDER BY created_at DESC LIMIT %" PRId32 " OFFSET %" PRId32 "", chat_id, limit, offset);

	return sql_get_list(db, query, lastrc);
}

msg_schema *msg_get_detail(sqlite3 *db, uint32_t msg_id, int *lastrc)
{
	msg_schema *res = (msg_schema *)calloc(1, sizeof(msg_schema));
	if (!res)
	{
		*lastrc = SQLITE_NOMEM;
		return NULL;
	}

	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "SELECT * FROM messages WHERE id=%" PRIu32 "", msg_id);

	sqlite3_stmt *stmt;
	int rc = sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return NULL;
	}

	if ((rc = sqlite3_step(stmt)) != SQLITE_ROW)
	{
		if (rc == SQLITE_DONE)
			*lastrc = SQLITE_EMPTY;
		else
			*lastrc = rc;
		sqlite3_finalize(stmt);
		return NULL;
	}

	res->id = msg_id;
	res->from_uid = sqlite3_column_int(stmt, 1);
	res->reply_to = sqlite3_column_int(stmt, 2);
	res->content_type = sqlite3_column_int(stmt, 3);
	res->content_length = sqlite3_column_int(stmt, 4);
	res->content = string_new(sqlite3_column_text(stmt, 5));
	res->created_at = sqlite3_column_int(stmt, 6);
	res->chat_id = sqlite3_column_int(stmt, 7);
	res->conv_id = sqlite3_column_int(stmt, 8);
	res->type = sqlite3_column_int(stmt, 9);

	sqlite3_finalize(stmt);

	*lastrc = SQLITE_OK;
	return res;
}

uint32_t msg_send(sqlite3 *db, const msg_schema *msg, int *lastrc)
{
	int rc;
	char reply_str[12];
	char conv_str[12];
	char chat_str[12];
	memset(reply_str, 0, 12);
	if (msg->reply_to == 0)
		snprintf(reply_str, 12, "NULL");
	else
		snprintf(reply_str, 12, "%" PRIu32 "", msg->reply_to);

	if (msg->conv_id == 0)
		snprintf(conv_str, 12, "NULL");
	else
		snprintf(conv_str, 12, "%" PRIu32 "", msg->conv_id);

	if (msg->chat_id == 0)
		snprintf(chat_str, 12, "NULL");
	else
		snprintf(chat_str, 12, "%" PRIu32 "", msg->chat_id);

	char query[QUERY_LARGE];

	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "PRAGMA foreign_keys=ON;");
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}

	memset(query, 0, QUERY_LARGE);
	snprintf(query, QUERY_LARGE,
					 "INSERT INTO messages(from_uid, reply_to, content_type, content_length, content, created_at, chat_id, conv_id, type) VALUES (%" PRIu32 ", %s, %d, %" PRIu32 ", '%s', %" PRIu32 ", %s, %s, %d);",
					 msg->from_uid, reply_str, msg->content_type, msg->content_length, msg->content, (uint32_t)time(NULL), chat_str, conv_str, MSG_SENT);

	pthread_mutex_lock(&mutex);
	rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	uint32_t newmsg = sqlite3_last_insert_rowid(db);
	if (rc != SQLITE_OK)
	{
		*lastrc = rc;
		return 0;
	}
	pthread_mutex_unlock(&mutex);

	*lastrc = SQLITE_OK;
	return newmsg;
}

void msg_delivered(sqlite3 *db, uint32_t msg_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "UPDATE messages type=%d WHERE id=%" PRIu32 ";", MSG_DELIVERED, msg_id);

	int rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
		*lastrc = rc;

	*lastrc = SQLITE_OK;
}

void msg_delete(sqlite3 *db, uint32_t msg_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "UPDATE messages SET content='', type=%d WHERE id=%" PRIu32 ";", MSG_DELETED, msg_id);

	int rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
		*lastrc = rc;

	*lastrc = SQLITE_OK;
}

void msg_drop(sqlite3 *db, uint32_t msg_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL, "DELETE FROM messages WHERE id=%" PRIu32 ";", msg_id);

	int rc = sqlite3_exec(db, query, NULL, NULL, NULL);
	if (rc != SQLITE_OK)
		*lastrc = rc;

	*lastrc = SQLITE_OK;
}

sllnode_t *msg_get_msg_sent(sqlite3 *db, uint32_t user_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	snprintf(query, QUERY_SMALL,
					 "select id from messages where chat_id in (select id from chats where member1=%" PRIu32 " or member2=%" PRIu32 ") and type=0 "
					 "union "
					 "select id from messages where conv_id in (select conv_id from members where user_id=%" PRIu32 ") and type=0;",
					 user_id, user_id, user_id);

	return sql_get_list(db, query, lastrc);
}

sllnode_t *msg_get_msg_del(sqlite3 *db, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, int *lastrc)
{
	char query[QUERY_SMALL];
	memset(query, 0, QUERY_SMALL);
	if (conv_id != 0)
		snprintf(query, QUERY_SMALL, "select id from messages where conv_id in (select conv_id from members where user_id=%" PRIu32 " and conv_id=%" PRIu32 ") and type=%d;", user_id, conv_id, MSG_DELETED);
	else if (chat_id != 0)
		snprintf(query, QUERY_SMALL, "select id from messages where chat_id in (select id from chats where (member1=%" PRIu32 " or member2=%" PRIu32 ") and id=%" PRIu32 ") and type=%d;", user_id, user_id, chat_id, MSG_DELETED);

	return sql_get_list(db, query, lastrc);
}

void msg_free(msg_schema *msg)
{
	if (!msg)
		return;
	string_remove(msg->content);
	free(msg);
}
