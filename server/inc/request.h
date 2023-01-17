#ifndef __REQUEST_H
#define __REQUEST_H

#include <stdint.h>

#define REQUEST_LEN				BUFSIZ
#define REQUEST_HEADER_LEN		64
#define REQUEST_BODY_LEN		(REQUEST_LEN-REQUEST_HEADER_LEN)
#define REQUEST_MSG_MAX_LEN		(REQUEST_BODY_LEN-12)

/* parsed request header*/

typedef struct
{
	/* Common header fields */
	int8_t group;		  /* byte 0 */
	int8_t action;		  /* byte 1 */
	uint8_t content_type; /* byte 2 */
	uint32_t content_len; /* byte 4-8 */
	uint32_t body_len;	  /* byte 8-12*/
	// uint32_t offset0;	  /* byte 12-16*/

	/* 16-byte token (byte 16-32) */
	char *token;

	/* 4-byte userid (byte 32-36) */
	uint32_t user_id;

	/* params for list retrieval */
	int32_t limit;	/* byte 36-40 */
	int32_t offset;	/* byte 40-44 */
} request_header;


/* parsed request body */

typedef struct
{
	char *uname;
	char *password;
	char *phone;
	char *email;
} request_auth;

typedef struct
{
	uint32_t user_id;
	char *uname;
} request_user;

typedef struct
{
	uint32_t conv_id;
	uint32_t user_id;
	char *gname;
} request_conv;

typedef struct
{
	uint32_t chat_id;
	uint32_t user_id2;
} request_chat;

typedef struct
{
	uint32_t conv_id, chat_id;
	uint32_t msg_id;
	uint32_t reply_id;
	char *msg_content;
} request_msg;

typedef union
{
	request_auth r_auth;
	request_user r_user;
	request_conv r_conv;
	request_chat r_chat;
	request_msg r_msg;
} request_body;

typedef struct
{
	request_header header;
	request_body *body;
} request;

request *request_parse(const char *buf);

void make_request_auth_register(const char *uname, const char *password, const char *phone, const char *email, char *res);
void make_request_auth_login(const char *uname, const char *password, char *res);
void make_request_user_logout(const char* token, uint32_t user_id, char *res);
void make_request_user_get_info(const char* token, uint32_t user_id, uint32_t user_id2, char *res);
void make_request_user_search(const char* token, uint32_t user_id, const char *uname, int32_t limit, int32_t offset, char *res);
void make_request_conv_create(const char* token, uint32_t user_id, const char* gname, char *res);
void make_request_conv_drop(const char* token, uint32_t user_id, uint32_t conv_id, char *res);
void make_request_conv_join(const char* token, uint32_t user_id, uint32_t conv_id, uint32_t user_id2, char *res);
void make_request_conv_quit(const char* token, uint32_t user_id, uint32_t conv_id, char *res);
void make_request_conv_get_info(const char* token, uint32_t user_id, uint32_t conv_id, char *res);
void make_request_conv_get_members(const char* token, uint32_t user_id, uint32_t conv_id, char *res);
void make_request_conv_get_list(const char* token, uint32_t user_id, int32_t limit, int32_t offset, char *res);
void make_request_chat_create(const char* token, uint32_t user_id, uint32_t user_id2, char *res);
void make_request_chat_delete(const char* token, uint32_t user_id, uint32_t chat_id, char *res);
void make_request_chat_get_list(const char* token, uint32_t user_id, int32_t limit, int32_t offset, char *res);
void make_request_msg_get_all(const char* token, uint32_t user_id, int32_t limit, int32_t offset, uint32_t conv_id, uint32_t chat_id, char *res);
void make_request_msg_get_detail(const char* token, uint32_t user_id, uint32_t msg_id, char *res);
void make_requests_msg_send_text(const char *token, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, uint32_t reply_to, const char *msg, char *res);
void make_requests_msg_send_file(const char *token, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, uint32_t reply_to, uint32_t fsize, const char *fname, char *res);
void make_request_msg_delete(const char* token, uint32_t user_id, uint32_t msg_id, char *res);
void make_request_msg_notify_new(const char* token, uint32_t user_id, char *res);
void make_request_msg_notify_del(const char* token, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, char *res);

void request_destroy(request *req);

#endif