#ifndef __RESPONSE_H
#define __RESPONSE_H

#include <stdint.h>

#define RESPONSE_LEN BUFSIZ
#define RESPONSE_HEADER_LEN 32
#define RESPONSE_BODY_LEN (RESPONSE_LEN - RESPONSE_HEADER_LEN)
#define RESPONSE_MSG_MAX_LEN (RESPONSE_BODY_LEN - 12)

/* parsed response header*/

typedef struct
{
	/* Common header fields */
	int8_t group;		  /* byte 0 */
	int8_t action;		  /* byte 1 */
	uint8_t content_type; /* byte 2 */
	uint8_t status_code;  /* byte 3*/
	uint32_t content_len; /* byte 4-8 */
	uint32_t body_len;	  /* byte 8-12*/
	// uint32_t offset;	  /* byte 12-16*/

	/* 4-byte userid (byte 16-24) */
	uint32_t count;
} response_header;

/* parsed response body */

typedef struct
{
	char *token;
	uint32_t user_id;
} response_auth;

typedef struct
{
	uint32_t *idls;
	char *username;
	char *phone;
	char *email;
} response_user;

typedef struct
{
	uint32_t *idls;
	uint32_t conv_id;
	uint32_t admin_id;
	char *gname;
} response_conv;

typedef struct
{
	uint32_t *idls;
	uint32_t chat_id;
} response_chat;

typedef struct
{
	uint32_t *idls;
	uint32_t msg_id;
	uint32_t conv_id, chat_id;
	uint32_t from_uid;
	uint32_t reply_to;
	char *msg_content;
} response_msg;

typedef union
{
	response_auth r_auth;
	response_user r_user;
	response_conv r_conv;
	response_chat r_chat;
	response_msg r_msg;
} response_body;

typedef struct
{
	response_header header;
	response_body *body;
} response;

response_auth response_get_auth_body(response *req);
response_user response_get_user_body(response *req);
response_conv response_get_conv_body(response *req);
response_chat response_get_chat_body(response *req);
response_msg response_get_msg_body(response *req);

response *response_parse(const char *buf);

void make_response_auth_register(uint8_t status, const char *token, uint32_t user_id, char *res);
void make_response_auth_login(uint8_t status, const char *token, uint32_t user_id, char *res);
void make_response_user_logout(uint8_t status, char *res);
void make_response_user_get_info(uint8_t status, const char *username, const char *phone, const char *email, char *res);
void make_response_user_search(uint8_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_conv_create(uint8_t status, uint32_t conv_id, char *res);
void make_response_conv_drop(uint8_t status, char *res);
void make_response_conv_join(uint8_t status, char *res);
void make_response_conv_quit(uint8_t status, char *res);
void make_response_conv_get_info(uint8_t status, uint32_t admin_id, const char *gname, char *res);
void make_response_conv_get_members(uint8_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_conv_get_list(uint8_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_chat_create(uint8_t status, uint32_t chat_id, char *res);
void make_response_chat_delete(uint8_t status, char *res);
void make_response_chat_get_list(uint8_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_msg_get_all(uint8_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_msg_get_detail(uint8_t status, uint8_t content_type, uint32_t msg_id, uint32_t conv_id, uint32_t chat_id, uint32_t from_uid, uint32_t reply_to, uint32_t fsize, const char *msg_content, char *res);
void make_responses_msg_send(uint8_t status, uint32_t msg_id, char *res);
void make_response_msg_delete(uint8_t status, char *res);
void make_response_msg_notify_new(uint8_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_msg_notify_del(uint8_t status, uint32_t count, const uint32_t *ls, char *res);

void response_destroy(response *res);

#endif