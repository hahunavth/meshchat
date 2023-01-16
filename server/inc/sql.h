#ifndef __SQL_H
#define __SQL_H

#include <stdint.h>

#include "sqlite3/sqlite3.h"
#include "utils/sll.h"

//////////////////////////////////////////////////

typedef struct
{
	uint32_t id;
	char *uname;
	char *password;
	char *phone;
	char *email;
} user_schema;

typedef struct
{
	uint32_t id;
	uint32_t member1;
	uint32_t member2;
} chat_schema;

typedef struct
{
	uint32_t id;
	uint32_t admin_id;
	char *name;
} conv_schema;

typedef struct
{
	uint32_t user_id;
	uint32_t conv_id;
} member_schema;

enum MSG_TYPE
{
	MSG_SENT,
	MSG_DELIVERED,
	MSG_DELETED
};

enum MSG_CONTENT_TYPE
{
	MSG_TEXT,
	MSG_FILE
};

typedef struct
{
	uint32_t id;
	uint32_t from_uid;
	uint32_t reply_to;
	int content_type;
	uint32_t content_length;
	char *content;
	uint32_t created_at;
	uint32_t conv_id;
	uint32_t chat_id;
	int type;
} msg_schema;

//////////////////////////////////////////////////

int sql_is_ok(int rc);
int sql_is_err(int rc);

//////////////////////////////////////////////////

user_schema *user_get_by_id(sqlite3 *db, uint32_t id, int *lastrc);
uint32_t user_create(sqlite3 *db, const user_schema *info, int *lastrc);
void user_drop(sqlite3 *db, uint32_t id, int *lastrc);
user_schema *user_get_by_uname(sqlite3 *db, const char *uname, int *lastrc);
sllnode_t *user_search_by_uname(sqlite3 *db, const char *uname, int limit, int offset, int *lastrc);
sllnode_t *user_get_conv_list(sqlite3 *db, uint32_t user_id, int limit, int offset, int *lastrc);
sllnode_t *user_get_chat_list(sqlite3 *db, uint32_t user_id, int limit, int offset, int *lastrc);

void user_free(user_schema *user);

//////////////////////////////////////////////////

uint32_t conv_create(sqlite3 *db, uint32_t admin_id, const char *name, int *lastrc);
int conv_is_admin(sqlite3 *db, uint32_t conv_id, uint32_t user_id, int *lastrc);
int conv_is_member(sqlite3 *db, uint32_t conv_id, uint32_t user_id, int *lastrc);
void conv_drop(sqlite3 *db, uint32_t conv_id, int *lastrc);
void conv_join(sqlite3 *db, uint32_t user_id, uint32_t conv_id, int *lastrc);
void conv_quit(sqlite3 *db, uint32_t user_id, uint32_t conv_id, int *lastrc);
conv_schema *conv_get_info(sqlite3 *db, uint32_t conv_id, int *lastrc);
sllnode_t *conv_get_members(sqlite3 *db, uint32_t conv_id, int *lastrc);

void conv_free(conv_schema *conv);

//////////////////////////////////////////////////

uint32_t chat_create(sqlite3 *db, uint32_t member1, uint32_t member2, int *lastrc);
int chat_is_member(sqlite3 *db, uint32_t chat_id, uint32_t user_id, int *lastrc);
void chat_drop(sqlite3 *db, uint32_t chat_id, int *lastrc);

void chat_free(chat_schema *chat);

//////////////////////////////////////////////////

sllnode_t *msg_conv_get_all(sqlite3 *db, uint32_t conv_id, int limit, int offset, int *lastrc);
sllnode_t *msg_chat_get_all(sqlite3 *db, uint32_t chat_id, int limit, int offset, int *lastrc);
msg_schema *msg_get_detail(sqlite3 *db, uint32_t msg_id, int *lastrc);
uint32_t msg_send(sqlite3 *db, const msg_schema *msg, int *lastrc);
void msg_delivered(sqlite3 *db, uint32_t msg_id, int *lastrc);
void msg_delete(sqlite3 *db, uint32_t msg_id, int *lastrc);
void msg_drop(sqlite3 *db, uint32_t msg_id, int *lastrc);

void msg_free(msg_schema *msg);

#endif