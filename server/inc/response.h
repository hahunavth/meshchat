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
	// uint32_t status_code;  /* byte 3*/
	uint32_t content_len; /* byte 4-8 */
	uint32_t body_len;	  /* byte 8-12*/
	uint32_t status_code; /* byte 12-16*/
	uint32_t count;		  /* byte 16-20*/
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
	char *uname;
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
	uint32_t created_at;
	uint8_t msg_type;
	uint8_t content_type;
	uint32_t content_length;
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

response *response_parse(const char *buf);

void make_err_response(uint32_t status, uint8_t group, uint8_t action, char *res);

void make_response_auth_register(uint32_t status, const char *token, uint32_t user_id, char *res);
void make_response_auth_login(uint32_t status, const char *token, uint32_t user_id, char *res);
void make_response_user_logout(uint32_t status, char *res);
void make_response_user_get_info(uint32_t status, const char *uname, const char *phone, const char *email, char *res);
void make_response_user_search(uint32_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_conv_create(uint32_t status, uint32_t conv_id, char *res);
void make_response_conv_drop(uint32_t status, char *res);
void make_response_conv_join(uint32_t status, char *res);
void make_response_conv_quit(uint32_t status, char *res);
void make_response_conv_get_info(uint32_t status, uint32_t admin_id, const char *gname, char *res);
void make_response_conv_get_members(uint32_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_conv_get_list(uint32_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_chat_create(uint32_t status, uint32_t chat_id, char *res);
void make_response_chat_delete(uint32_t status, char *res);
void make_response_chat_get_list(uint32_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_msg_get_all(uint32_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_msg_get_detail(uint32_t status, const response_msg *msg, char *res);
void make_responses_msg_send(uint32_t status, uint32_t msg_id, char *res);
void make_response_msg_delete(uint32_t status, char *res);
void make_response_msg_notify_new(uint32_t status, uint32_t count, const uint32_t *ls, char *res);
void make_response_msg_notify_del(uint32_t status, uint32_t count, const uint32_t *ls, char *res);

void response_destroy(response *res);

#endif