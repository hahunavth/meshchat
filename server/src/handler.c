#include "handler.h"
#include "request.h"
#include "response.h"
#include "sql.h"
#include "sqlite3/sqlite3.h"
#include "utils/sll.h"

#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/poll.h>
#include <sys/epoll.h>
#include <time.h>
#include <unistd.h>

#include <openssl/blowfish.h>
#include <openssl/md5.h>

#define EXPIRY_TIME 108000
#define TOKEN_LEN 16
#define HASHED_LEN 16

sqlite3 *db;
static BF_KEY key;
static void (*close_sock)(int);

#define SEND_RESPONSE()                        \
	{                                          \
		if (write(cfd, buf, BUFSIZ) != BUFSIZ) \
		{                                      \
			perror("write() failed");          \
			close_sock(cfd);                   \
			*close_conn = 1;                   \
		}                                      \
	}

#define RESPONSE_ERR(status, group, action)                      \
	{                                                            \
		printf("response errror on line %d\n", __LINE__);        \
		make_err_response((uint32_t)status, group, action, buf); \
		SEND_RESPONSE();                                         \
		return;                                                  \
	}

#define RESPONSE_ERR_FREE(status, group, action, resource, freefn) \
	{                                                              \
		freefn(resource);                                          \
		RESPONSE_ERR(status, group, action);                       \
	}

int init_handler(const char *db_file, const char *secrete_key, void (*close_sock_fn)(int))
{
	/* Connect to db */
	if (sqlite3_open(db_file, &db) != SQLITE_OK)
	{
		fprintf(stderr, "Can't open database: %s\n", sqlite3_errmsg(db));
		return 0;
	}
	puts("Connected to database");

	/* Set up secret key */
	BF_set_key(&key, strlen(secrete_key), (const unsigned char *)secrete_key);

	close_sock = close_sock_fn;

	return 1;
}

void destroy_handler()
{
	sqlite3_close(db);
}

/*******************************/

static void make_token(in_addr_t addr, uint32_t user_id, char *res)
{
	char msg[16];
	memcpy(msg, &addr, 4);
	memcpy(msg + 4, &user_id, 4);
	time_t expiry = time(NULL) + EXPIRY_TIME;
	memcpy(msg + 8, &expiry, sizeof(time_t));

	BF_ecb_encrypt((const unsigned char *)msg, (unsigned char *)res, &key, BF_ENCRYPT);
	BF_ecb_encrypt((const unsigned char *)(msg + 8), (unsigned char *)(res + 8), &key, BF_ENCRYPT);
}

int verify_token(uint32_t addr, uint32_t user_id, const char *token)
{
	char decrypt[16];
	time_t expiry;

	BF_ecb_encrypt((const unsigned char *)token, (unsigned char *)decrypt, &key, BF_DECRYPT);
	BF_ecb_encrypt((const unsigned char *)(token + 8), (unsigned char *)(decrypt + 8), &key, BF_DECRYPT);

	if (memcmp(decrypt, &addr, 4) != 0)
		return -1;
	if (memcmp(decrypt + 4, &user_id, 4) != 0)
		return -1;
	memcpy(&expiry, decrypt + 8, sizeof(time_t));
	// printf("addr = %u, user_id = %u, expiry = %lu\n", addr, user_id, expiry);
	if (expiry - time(NULL) > EXPIRY_TIME)
		return 0;
	return 1;
}

static int hash_str(const char *str, char *res)
{
	MD5_CTX md5_ctx;
	char hashed[HASHED_LEN];

	if (MD5_Init(&md5_ctx) == 0)
		return -1;

	if (MD5_Update(&md5_ctx, str, strlen(str)) == 0)
		return -1;

	if (MD5_Final((unsigned char *)hashed, &md5_ctx) == 0)
		return -1;

	/* output a hexa string */
	for (int i = 0; i < HASHED_LEN; i++)
	{
		uint8_t b = hashed[i];
		sprintf(res + (i << 1), "%02x", b);
	}
	res[(HASHED_LEN << 1)] = '\0';

	return 0;
}

/*******************************/

void handle_auth_register(int cfd, in_addr_t addr, request *req, char *buf, int *close_conn)
{
	int rc;
	char token[TOKEN_LEN];
	char hashed_password[(HASHED_LEN << 1) + 1];

	request_auth *ra = &((req->body)->r_auth);

	if (hash_str(ra->password, hashed_password) < 0)
		RESPONSE_ERR(500, 0, 0);

	user_schema user = {
		.uname = ra->uname,
		.password = hashed_password, /* Hash password before saving */
		.email = ra->email,
		.phone = ra->phone};

	/* Save the new user to db */
	uint32_t id = user_create(db, &user, &rc);
	if (sql_is_err(rc))
	{
		if (rc == SQLITE_CONSTRAINT)
			RESPONSE_ERR(409, 0, 0); /* An user with such uname has already existed*/
		RESPONSE_ERR(500, 0, 0);
	}

	make_token(addr, id, token);
	make_response_auth_register(201, token, id, buf);
	SEND_RESPONSE();
}

void handle_auth_login(int cfd, in_addr_t addr, request *req, char *buf, int *close_conn)
{
	int rc;
	char token[TOKEN_LEN];
	char hashed_password[(HASHED_LEN << 1) + 1];

	request_auth *ra = &((req->body)->r_auth);

	user_schema *user = user_get_by_uname(db, ra->uname, &rc);

	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 0, 1, user, user_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 0, 1, user, user_free);

	hash_str(ra->password, hashed_password);
	if (strcmp(hashed_password, user->password) != 0)
		RESPONSE_ERR_FREE(403, 0, 1, user, user_free);

	make_token(addr, user->id, token);
	make_response_auth_login(200, token, user->id, buf);
	SEND_RESPONSE();

	user_free(user);
}

/****************/

void handle_user_get_info(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_user *ru = &(req->body->r_user);
	user_schema *user = user_get_by_id(db, ru->user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 1, 1, user, user_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 1, 1, user, user_free);

	make_response_user_get_info(200, user->uname, user->phone, user->email, buf);
	SEND_RESPONSE();

	user_free(user);
}

void handle_user_search(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	request_user *ru = &(req->body->r_user);
	sllnode_t *ls = user_search_by_uname(db, ru->uname, limit, offset, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 1, 2, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_user_search(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

/****************/

void handle_conv_create(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	uint32_t id = conv_create(db, (req->header).user_id, (req->body->r_conv).gname, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 0);

	make_response_conv_create(201, id, buf);
	SEND_RESPONSE();
}

void handle_conv_drop(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_admin = conv_is_admin(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_admin)
		RESPONSE_ERR(403, 2, 4);

	conv_drop(db, rconv->conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 1);

	make_response_conv_drop(200, buf);
	SEND_RESPONSE();
}

void handle_conv_join(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_member = conv_is_admin(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_member)
		RESPONSE_ERR(403, 2, 4);

	conv_join(db, rconv->user_id, rconv->conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 2);

	make_response_conv_join(200, buf);
	SEND_RESPONSE();
}

void handle_conv_quit(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_admin = conv_is_admin(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);

	int is_member = conv_is_member(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);

	if ((!is_member) || (is_member & is_admin))
		RESPONSE_ERR(403, 2, 4);

	conv_quit(db, (req->header).user_id, (req->body->r_conv).conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 3);

	make_response_conv_quit(200, buf);
	SEND_RESPONSE();
}

void handle_conv_get_info(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	conv_schema *conv = conv_get_info(db, (req->body->r_conv).conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 2, 4, conv, conv_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 2, 4, conv, conv_free);

	make_response_conv_get_info(200, conv->admin_id, conv->name, buf);
	SEND_RESPONSE();
}

void handle_conv_get_members(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_member = conv_is_member(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_member)
		RESPONSE_ERR(403, 2, 4);

	sllnode_t *ls = conv_get_members(db, rconv->conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 2, 4, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_conv_get_members(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

void handle_conv_get_list(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	sllnode_t *ls = user_get_conv_list(db, (req->header).user_id, limit, offset, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 2, 6, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_conv_get_list(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

/****************/

void handle_chat_create(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_chat *rchat = &(req->body->r_chat);

	if ((req->header).user_id == rchat->user_id2)
		RESPONSE_ERR(403, 3, 0);

	uint32_t id = chat_create(db, (req->header).user_id, rchat->user_id2, &rc);
	if (sql_is_err(rc))
	{
		if (rc == SQLITE_CONSTRAINT)
			RESPONSE_ERR(409, 3, 0); /* The chat has already existed */
		RESPONSE_ERR(500, 3, 0);
	}

	make_response_chat_create(201, id, buf);
	SEND_RESPONSE();
}

void handle_chat_drop(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_chat *rchat = &(req->body->r_chat);
	int is_member = chat_is_member(db, rchat->chat_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 3, 1);
	if (!is_member)
		RESPONSE_ERR(403, 3, 1);

	chat_drop(db, rchat->chat_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 3, 1);

	make_response_chat_delete(200, buf);
	SEND_RESPONSE();
}

void handle_chat_get_list(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	sllnode_t *ls = user_get_chat_list(db, (req->header).user_id, limit, offset, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 3, 2, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_chat_get_list(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

/****************/

void handle_msg_get_all(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	uint32_t chat_id = (req->body->r_msg).chat_id;
	uint32_t conv_id = (req->body->r_msg).conv_id;

	int is_member = conv_is_member(db, conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 4, 0);

	if (!is_member)
	{
		is_member = chat_is_member(db, chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR(500, 4, 0);
		if (!is_member)
			RESPONSE_ERR(403, 4, 0);
	}

	sllnode_t *ls;
	if (chat_id > 0)
		ls = msg_chat_get_all(db, chat_id, limit, offset, &rc);
	else
		ls = msg_conv_get_all(db, conv_id, limit, offset, &rc);

	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 0, &ls, sll_remove);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 0, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_msg_get_all(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

void handle_msg_get_detail(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;

	msg_schema *msg = msg_get_detail(db, (req->body->r_msg).msg_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 1, msg, msg_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 1, msg, msg_free);

	int is_member = conv_is_member(db, msg->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 1, msg, msg_free);

	if (!is_member)
	{
		is_member = chat_is_member(db, msg->chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR_FREE(500, 4, 1, msg, msg_free);
		if (!is_member)
			RESPONSE_ERR_FREE(403, 4, 1, msg, msg_free);
	}

	response_msg rm = {
		.msg_id = msg->id, .from_uid = msg->from_uid, .reply_to = msg->reply_to, .conv_id = msg->conv_id, .chat_id = msg->chat_id, .created_at = msg->created_at, .msg_type = msg->type, .content_length = msg->content_length, .content_type = msg->content_type, .msg_content = msg->content};
	make_response_msg_get_detail(200, &rm, buf);
	SEND_RESPONSE();

	msg_delivered(db, (req->body->r_msg).msg_id, &rc);

	msg_free(msg);
}

void process_fname(const char *fname, char *res)
{
	int fname_len = strlen(fname);
	char _fname[1024];
	memcpy(_fname, fname, fname_len + 1);

	int i;
	for (i = fname_len - 1; i >= 0; i--)
	{
		if (fname[i] == '.')
		{
			snprintf(_fname, i + 1, "%s", fname);
			break;
		}
	}

	i = i < 0 ? 0 : i;
	snprintf(_fname + i, 20, "%ld", time(NULL));
	// printf("%s\n", _fname);
	hash_str(_fname, res);
	strcpy(res + (HASHED_LEN << 1), i == 0 ? "" : (fname + i));
}

#define ERROR_HANDLING() \
	close(fd);           \
	remove(fname);       \
	close_sock(cfd);     \
	*close_conn = 1;     \
	return;

void handle_msg_send(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	request_msg *rm = &(req->body->r_msg);
	int content_type = (req->header).content_type;
	uint32_t content_len = (req->header).content_len;

	int is_member = conv_is_member(db, rm->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 4, 3);

	if (!is_member)
	{
		is_member = chat_is_member(db, rm->chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR(500, 4, 3);
		if (!is_member)
			RESPONSE_ERR(403, 4, 3);
	}

	msg_schema msg = {
		.from_uid = (req->header).user_id, .reply_to = rm->reply_id, .conv_id = rm->conv_id, .chat_id = rm->chat_id, .content_type = content_type, .content = rm->msg_content};

	if (content_type == MSG_TEXT)
	{
		msg.content_length = content_len - 12;
		uint32_t id = msg_send(db, &msg, &rc);
		if (sql_is_err(rc))
		{
			if (rc == SQLITE_CONSTRAINT)
				RESPONSE_ERR(409, 4, 2); /* conflicted from_uid, reply_to, chat_id, conv_id */
			RESPONSE_ERR(500, 4, 2);
		}
		make_responses_msg_send(201, id, buf);
		SEND_RESPONSE();
	}
	else if (content_type == MSG_FILE)
	{
		puts("Start receiving file");
		ssize_t fsize = content_len;
		char fname[1024];
		process_fname((req->body->r_msg).msg_content, buf);
		snprintf(fname, BUFSIZ, "./storage/%s", buf);

		int fd = open(fname, O_CREAT | O_EXCL | O_WRONLY, 0644);
		if (fd < 0)
		{
			perror("open() failed");
			close_sock(cfd);
			RESPONSE_ERR(500, 4, 2);
		}

		ssize_t nbytes;
		struct pollfd pfd;
		pfd.fd = cfd;
		pfd.events = POLLIN;
		do
		{
			int rc = poll(&pfd, 1, 10 * 1000);
			if (rc < 0 || (pfd.revents != POLLIN))
			{
				perror("poll() failed");
				close(fd);
				RESPONSE_ERR(500, 4, 2);
			}
			if (rc == 0)
			{
				puts("poll() timed out");
				ERROR_HANDLING();
			}
			nbytes = read(cfd, buf, BUFSIZ);
			if (nbytes < 0)
			{
				if (errno != EWOULDBLOCK)
				{
					puts("read() error");
					ERROR_HANDLING();
				}
				continue;
			}
			if (nbytes == 0 && fsize > 0)
			{
				puts("file not received");
				ERROR_HANDLING();
				return;
			}
			if (write(fd, buf, nbytes) != nbytes)
			{
				perror("write() to fs failed");
				ERROR_HANDLING();
			}
			fsize -= nbytes;
			if (fsize <= 0)
				break;
		} while (nbytes > 0);

		if (close(fd) < 0)
		{
			remove(fname);
			perror("close() failed to close file");
			RESPONSE_ERR(500, 4, 2);
		}
		puts("Successfully received file");

		puts("Saving to db");
		msg.content_length = content_len;
		msg.content = fname;
		uint32_t id = msg_send(db, &msg, &rc);
		if (sql_is_err(rc))
		{
			remove(fname);
			if (rc == SQLITE_CONSTRAINT)
				RESPONSE_ERR(409, 4, 2); /* conflicted from_uid, reply_to, chat_id, conv_id */
			RESPONSE_ERR(500, 4, 2);
		}
		puts("Saved to db");
		make_responses_msg_send(201, id, buf);
		SEND_RESPONSE();
		puts("Sent response");
	}
	else
		RESPONSE_ERR(400, 4, 2);
}

void handle_msg_delete(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	msg_schema *msg = msg_get_detail(db, (req->body->r_msg).msg_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 3, msg, msg_free);

	int is_member = conv_is_member(db, msg->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);

	if (!is_member)
	{
		is_member = chat_is_member(db, msg->chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);
		if (!is_member)
			RESPONSE_ERR_FREE(403, 4, 3, msg, msg_free);
	}

	msg_delete(db, msg->id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);

	make_response_msg_delete(200, buf);
	SEND_RESPONSE();
	msg_free(msg);
}

void handle_notify_new_msg(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	sllnode_t *ls = msg_get_msg_sent(db, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 4, 4);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_msg_notify_new(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

void handle_notify_del_msg(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	uint32_t conv_id = (req->body->r_msg).conv_id;
	uint32_t chat_id = (req->body->r_msg).chat_id;

	sllnode_t *ls = msg_get_msg_del(db, (req->header).user_id, conv_id, chat_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 4, 4);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_msg_notify_del(200, len, idls, buf);
	SEND_RESPONSE();

	sll_remove(&ls);
}

void handle_msg_download_file(int cfd, request *req, char *buf, int *close_conn)
{
	int rc;
	msg_schema *msg = msg_get_detail(db, (req->body->r_msg).msg_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 6, msg, msg_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 6, msg, msg_free);

	int is_member = conv_is_member(db, msg->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 6, msg, msg_free);

	if (!is_member)
	{
		is_member = chat_is_member(db, msg->chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR_FREE(500, 4, 6, msg, msg_free);
		if (!is_member)
			RESPONSE_ERR_FREE(403, 4, 6, msg, msg_free);
	}

	if (msg->content_type != MSG_FILE)
		RESPONSE_ERR_FREE(400, 4, 6, msg, msg_free);

	make_response_msg_download_file(200, buf);
	if (write(cfd, buf, BUFSIZ) != BUFSIZ)
	{
		msg_free(msg);
		close_sock(cfd);
		*close_conn = 1;
		return;
	}

	char fname[strlen(msg->content) + 1];
	strcpy(fname, msg->content);
	msg_free(msg);

	int fd = open(fname, O_RDONLY);
	struct stat sb;
	if (fd < 0)
	{
		perror("open() failed");
		RESPONSE_ERR(500, 4, 6);
	}

	rc = stat(msg, &sb);
	if (rc < 0)
	{
		close(fd);
		perror("stat() failed");
		RESPONSE_ERR(500, 4, 6);
	}

	ssize_t fsize = sb.st_size;
	ssize_t nbytes;
	struct pollfd pfd;
	pfd.fd = cfd;
	pfd.events = POLLOUT;
	do
	{
		if ((nbytes = read(fd, buf, BUFSIZ)) < 0)
		{
			perror("read() from file failed");
			ERROR_HANDLING();
		}

		int rc = poll(&pfd, 1, 10 * 1000);
		if (rc < 0 || (pfd.revents != POLLOUT))
		{
			perror("poll() failed");
			close(fd);
			RESPONSE_ERR(500, 4, 2);
		}
		if (rc == 0)
		{
			puts("poll() timed out");
			ERROR_HANDLING();
		}

		if (write(cfd, buf, nbytes) != nbytes)
		{
			perror("write() send file failed");
			ERROR_HANDLING();
		}
		fsize -= nbytes;
		if (fsize <= 0)
			break;
	} while (nbytes > 0);

	close(fd);
}

/*******************************/

void handle_req(int epoll_fd, int cfd)
{
	int rc, close_conn = 0;
	char buf[BUFSIZ + 1];
	struct sockaddr_in addr;
	socklen_t socklen = sizeof(addr);
	ssize_t nbytes;

	printf("Thread #%lu working on %d\n", pthread_self(), cfd);

	if ((nbytes = recv(cfd, buf, BUFSIZ, 0)) < 0)
	{
		perror("recv() failed");
		close_sock(cfd);
		return;
	}

	if (nbytes == 0)
	{
		printf("Connection closed on %d\n", cfd);
		close_sock(cfd);
		return;
	}

	if (getpeername(cfd, (struct sockaddr *)&addr, &socklen) >= 0)
	{
		char addr_str[INET_ADDRSTRLEN];
		if (inet_ntop(AF_INET, &(addr.sin_addr), addr_str, sizeof(addr)))
			printf("*** [#%lu] [%s:%hu] -> server: %ld bytes\n", pthread_self(), addr_str, ntohs(addr.sin_port), nbytes);
	}

	request *req = request_parse(buf);
	if (!req)
	{
		make_err_response((uint32_t)400, 0xFF, 0xFF, buf);
		if (write(cfd, buf, BUFSIZ) < 0)
		{
			perror("write() failed");
			close_sock(cfd);
		}
	}
	else
	{
		request_header *header = &(req->header);
		if (header->group == 0)
		{
			switch (header->action)
			{
			case 0x00:
				handle_auth_register(cfd, addr.sin_addr.s_addr, req, buf, &close_conn);
				break;
			case 0x01:
				handle_auth_login(cfd, addr.sin_addr.s_addr, req, buf, &close_conn);
				break;
			}
		}
		else
		{
			rc = verify_token(addr.sin_addr.s_addr, header->user_id, header->token);
			if (rc <= 0)
			{
				make_err_response((uint32_t)403, (header->group), (header->action), buf);
				if (write(cfd, buf, BUFSIZ) < 0)
				{
					perror("write() failed");
					close_sock(cfd);
				}
			}
			else
			{
				switch (header->group)
				{
				case 0x01:
					switch (header->action)
					{
					case 0x00:
						make_response_user_logout(200, buf);
						if (write(cfd, buf, BUFSIZ) < 0)
							perror("write() failed");
						close(cfd);
						close_conn = 1;
						break;
					case 0x01:
						handle_user_get_info(cfd, req, buf, &close_conn);
						break;
					case 0x02:
						handle_user_search(cfd, req, buf, &close_conn);
						break;
					}
					break;
				case 0x02:
					switch (header->action)
					{
					case 0x00:
						handle_conv_create(cfd, req, buf, &close_conn);
						break;
					case 0x01:
						handle_conv_drop(cfd, req, buf, &close_conn);
						break;
					case 0x02:
						handle_conv_join(cfd, req, buf, &close_conn);
						break;
					case 0x03:
						handle_conv_quit(cfd, req, buf, &close_conn);
						break;
					case 0x04:
						handle_conv_get_info(cfd, req, buf, &close_conn);
						break;
					case 0x05:
						handle_conv_get_members(cfd, req, buf, &close_conn);
						break;
					case 0x06:
						handle_conv_get_list(cfd, req, buf, &close_conn);
						break;
					}
					break;
				case 0x03:
					switch (header->action)
					{
					case 0x00:
						handle_chat_create(cfd, req, buf, &close_conn);
						break;
					case 0x01:
						handle_chat_drop(cfd, req, buf, &close_conn);
						break;
					case 0x02:
						handle_chat_get_list(cfd, req, buf, &close_conn);
						break;
					}
					break;
				case 0x04:
					switch (header->action)
					{
					case 0x00:
						handle_msg_get_all(cfd, req, buf, &close_conn);
						break;
					case 0x01:
						handle_msg_get_detail(cfd, req, buf, &close_conn);
						break;
					case 0x02:
						handle_msg_send(cfd, req, buf, &close_conn);
						break;
					case 0x03:
						handle_msg_delete(cfd, req, buf, &close_conn);
						break;
					case 0x04:
						handle_notify_new_msg(cfd, req, buf, &close_conn);
						break;
					case 0x05:
						handle_notify_del_msg(cfd, req, buf, &close_conn);
						break;
					case 0x06:
						handle_msg_download_file(cfd, req, buf, &close_conn);
						break;
					}
					break;
				}
			}
		}
	}

	/* Rearm the socket event */
	if (close_conn == 0)
	{
		struct epoll_event epevent;
		epevent.events = EPOLLIN | EPOLLET | EPOLLONESHOT;
		epevent.data.fd = cfd;
		if (epoll_ctl(epoll_fd, EPOLL_CTL_MOD, cfd, &epevent) < 0)
		{
			perror("epoll_ctl(2) failed attempting to add new client");
			close_sock(cfd);
		}
		else
			printf("fd %d rearmed\n", cfd);
	}

	request_destroy(req);
}
