#include "response.h"
#include "utils/string.h"

#include <arpa/inet.h>
#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define TOKEN_LEN 16
#define EMPTY_TOKEN "\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06"
#define BODY_DEMLIMITER '\x1D'

static uint32_t parse_uint32_from_buf(const char *buf)
{
	uint32_t num;
	memcpy(&num, buf, 4);
	return ntohl(num);
}

static void write_uint32_to_buf(char *buf, uint32_t num)
{
	num = htonl(num);
	memcpy(buf, &num, 4);
}

static uint32_t *parse_uint32_list(char *buf, uint32_t count)
{
	size_t sz = count << 2;
	uint32_t *ls = (uint32_t *)malloc(sz); // 4xcount
	assert(ls);
	for (uint32_t i = 0; i < count; i++)
	{
		uint32_t cur;
		memcpy(&cur, buf + (i << 2), 4);
		ls[i] = ntohl(cur);
	}
	return ls;
}

static void response_parse_auth(response *req, const char *body);
static void response_parse_user(response *req, const char *body);
static void response_parse_conv(response *req, const char *body);
static void response_parse_chat(response *req, const char *body);
static void response_parse_msg(response *req, const char *body);

void response_body_destroy(response_body *body, int8_t group);

response *response_parse(const char *buf)
{
	response *req = (response *)calloc(1, sizeof(response));
	assert(req);

	response_header *header = &(req->header);
	req->body = NULL;

	header->group = buf[0];
	header->action = buf[1];
	header->content_type = buf[2];
	// header->status_code = buf[3];
	header->content_len = parse_uint32_from_buf(buf + 4);
	header->body_len = parse_uint32_from_buf(buf + 8);
	header->status_code = parse_uint32_from_buf(buf + 12);
	header->count = parse_uint32_from_buf(buf + 16);

	switch (header->group)
	{
	case 0:
		response_parse_auth(req, buf + RESPONSE_HEADER_LEN);
		break;
	case 1:
		response_parse_user(req, buf + RESPONSE_HEADER_LEN);
		break;
	case 2:
		response_parse_conv(req, buf + RESPONSE_HEADER_LEN);
		break;
	case 3:
		response_parse_chat(req, buf + RESPONSE_HEADER_LEN);
		break;
	case 4:
		response_parse_msg(req, buf + RESPONSE_HEADER_LEN);
		break;
	default:
		free(req);
		return NULL;
	}
	return req;
}

static char *field_tokenizer(char *buf, char **rest)
{
	char *old = buf;
	for (; (*buf) != '\0'; buf++)
	{
		if (buf[0] == BODY_DEMLIMITER)
		{
			*rest = buf + 1;
			*buf = '\0';
			break;
		}
	}
	return old;
}

void response_parse_auth(response *req, const char *body)
{
	// response_header *header = &(req->header);

	response_body *rb = (response_body *)calloc(1, sizeof(response_body));
	assert(rb);

	(rb->r_auth).token = string_mem_n(body, TOKEN_LEN);
	(rb->r_auth).user_id = parse_uint32_from_buf(body + TOKEN_LEN);

	req->body = rb;
}

void response_parse_user(response *req, const char *body)
{
	response_header *header = &(req->header);

	response_body *rb = (response_body *)calloc(1, sizeof(response_body));
	assert(rb);

	switch (header->action)
	{
	case 1:
	{
		uint32_t bodylen = header->body_len;
		char _body[bodylen + 1];
		memcpy(_body, body, bodylen);
		_body[bodylen] = '\0';

		char *rest = _body;
		char *uname = field_tokenizer(rest, &rest);
		char *phone = field_tokenizer(rest, &rest);
		char *email = field_tokenizer(rest, &rest);

		(rb->r_user).uname = string_new(uname);
		(rb->r_user).phone = string_new(phone);
		(rb->r_user).email = string_new(email);
	}
	break;
	case 2:
		(rb->r_user).idls = parse_uint32_list(body, header->count);
		break;
	default:
		response_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void response_parse_conv(response *req, const char *body)
{
	response_header *header = &(req->header);

	response_body *rb = (response_body *)calloc(1, sizeof(response_body));
	assert(rb);

	switch (header->action)
	{
	case 0:
		(rb->r_conv).conv_id = parse_uint32_from_buf(body);
		break;
	case 4:
		(rb->r_conv).admin_id = parse_uint32_from_buf(body);
		(rb->r_conv).gname = string_new_n(body + 4, (header->body_len) - 4);
		break;
	case 5:
	case 6:
		(rb->r_conv).idls = parse_uint32_list(body, header->count);
		break;
	default:
		response_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void response_parse_chat(response *req, const char *body)
{
	response_header *header = &(req->header);

	response_body *rb = (response_body *)calloc(1, sizeof(response_body));
	assert(rb);

	switch (header->action)
	{
	case 0:
		(rb->r_chat).chat_id = parse_uint32_from_buf(body);
		break;
	case 2:
		(rb->r_chat).idls = parse_uint32_list(body, header->count);
		break;
	default:
		response_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void response_parse_msg(response *req, const char *body)
{
	response_header *header = &(req->header);

	response_body *rb = (response_body *)calloc(1, sizeof(response_body));
	assert(rb);

	response_msg *rm = &(rb->r_msg);

	switch (header->action)
	{
	case 0:
	case 4:
	case 5:
		rm->idls = parse_uint32_list(body, header->count);
		break;
	case 1:
		rm->msg_id = parse_uint32_from_buf(body);
		rm->from_uid = parse_uint32_from_buf(body + 4);
		rm->reply_to = parse_uint32_from_buf(body + 8);
		rm->conv_id = parse_uint32_from_buf(body + 12);
		rm->chat_id = parse_uint32_from_buf(body + 16);
		rm->created_at = parse_uint32_from_buf(body + 20);
		rm->msg_type = body[24];
		rm->content_type = body[25];
		rm->content_length = parse_uint32_from_buf(body + 26);
		rm->msg_content = string_new_n(body + 30, header->body_len - 20);
		break;
	case 2:
		rm->msg_id = parse_uint32_from_buf(body);
		;
		break;
	default:
		response_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void response_body_destroy(response_body *body, int8_t group)
{
	if (!body)
		return;
	switch (group)
	{
	case 0:
	{
		response_auth ra = body->r_auth;
		if (ra.token)
			string_remove(ra.token);
	}
	break;
	case 1:
	{
		response_user ru = body->r_user;
		if (ru.idls)
			free(ru.idls);
		if (ru.uname)
			string_remove(ru.uname);
		if (ru.phone)
			string_remove(ru.phone);
		if (ru.email)
			string_remove(ru.email);
	}
	break;
	case 2:
	{
		response_conv rc = body->r_conv;
		if (rc.idls)
			free(rc.idls);
		if (rc.gname)
			string_remove(rc.gname);
	}
	break;
	case 3:
		if ((body->r_chat).idls)
			free((body->r_chat).idls);
		break;
	case 4:
	{
		response_msg rm = body->r_msg;
		if (rm.idls)
			free(rm.idls);
		if (rm.msg_content)
			string_remove(rm.msg_content);
	}
	break;
	}
	free(body);
}

void response_destroy(response *req)
{
	if (!req)
		return;
	response_body_destroy(req->body, (req->header).group);
	free(req);
}

//////////////////////////////////////////////////

void make_err_response(uint32_t status, uint8_t group, uint8_t action, char *res)
{
	memset(res, 0, RESPONSE_HEADER_LEN);

	res[0] = group;
	res[1] = action;
	write_uint32_to_buf(res + 12, status);
}

static void make_response_header(response_header *header, char *res)
{
	memset(res, 0, RESPONSE_HEADER_LEN);

	res[0] = header->group;
	res[1] = header->action;
	res[2] = header->content_type;
	// res[3] = header->status_code;

	write_uint32_to_buf(res + 4, header->content_len);
	write_uint32_to_buf(res + 8, header->body_len);
	write_uint32_to_buf(res + 12, header->status_code);
	write_uint32_to_buf(res + 16, header->count);
}

static void make_response_empty_body(uint8_t group, uint8_t action, uint32_t status_code, char *res)
{
	response_header header = {
			.group = group,
			.action = action,
			.content_type = 0,
			.status_code = status_code,
			.content_len = 0,
			.body_len = 0,
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);
}

static void make_response_id_list(uint8_t group, uint8_t action, uint32_t status_code, const uint32_t *ls, uint32_t count, char *res)
{
	uint32_t sz = (count << 2);
	response_header header = {
			.group = group,
			.action = action,
			.content_type = 0,
			.status_code = status_code,
			.content_len = sz,
			.body_len = sz,
			// .offset = 0,
			.count = count};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	for (uint32_t i = 0; i < count; i++)
	{
		uint32_t cur = htonl(ls[i]);
		memcpy(body + (i << 2), &cur, 4);
	}
}

static void make_response_uint32(uint8_t group, uint8_t action, uint32_t status_code, uint32_t num, char *res)
{
	response_header header = {
			.group = group,
			.action = action,
			.content_type = 0,
			.status_code = status_code,
			.content_len = 4,
			.body_len = 4,
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	write_uint32_to_buf(body, num);
}

void make_response_auth_register(uint32_t status_code, const char *token, uint32_t user_id, char *res)
{
	response_header header = {
			.group = 0,
			.action = 0,
			.content_type = 0,
			.status_code = status_code,
			.content_len = TOKEN_LEN + 4,
			.body_len = TOKEN_LEN + 4,
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	memcpy(body, token, TOKEN_LEN);
	write_uint32_to_buf(body + TOKEN_LEN, user_id);
}

void make_response_auth_login(uint32_t status_code, const char *token, uint32_t user_id, char *res)
{
	response_header header = {
			.group = 0,
			.action = 1,
			.content_type = 0,
			.status_code = status_code,
			.content_len = TOKEN_LEN + 4,
			.body_len = TOKEN_LEN + 4,
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	memcpy(body, token, TOKEN_LEN);
	write_uint32_to_buf(body + TOKEN_LEN, user_id);
}

inline void make_response_user_logout(uint32_t status_code, char *res)
{
	make_response_empty_body(1, 0, status_code, res);
}

void make_response_user_get_info(uint32_t status_code, const char *uname, const char *phone, const char *email, char *res)
{
	size_t uname_len = strlen(uname);
	size_t phone_len = strlen(phone);
	size_t email_len = strlen(email);
	size_t sum_len = uname_len + phone_len + email_len;

	response_header header = {
			.group = 1,
			.action = 1,
			.content_type = 0,
			.status_code = status_code,
			.content_len = sum_len + 2,
			.body_len = sum_len + 2,
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	memcpy(body, uname, uname_len);
	body[uname_len] = BODY_DEMLIMITER;
	body += (uname_len + 1);
	memcpy(body, phone, phone_len);
	body[phone_len] = BODY_DEMLIMITER;
	body += (phone_len + 1);
	memcpy(body, email, email_len);
}

inline void make_response_user_search(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(1, 2, status_code, ls, count, res);
}

inline void make_response_conv_create(uint32_t status_code, uint32_t conv_id, char *res)
{
	make_response_uint32(2, 0, status_code, conv_id, res);
}

inline void make_response_conv_drop(uint32_t status_code, char *res)
{
	make_response_empty_body(2, 1, status_code, res);
}

inline void make_response_conv_join(uint32_t status_code, char *res)
{
	make_response_empty_body(2, 2, status_code, res);
}

inline void make_response_conv_quit(uint32_t status_code, char *res)
{
	make_response_empty_body(2, 3, status_code, res);
}

void make_response_conv_get_info(uint32_t status_code, uint32_t admin_id, const char *gname, char *res)
{
	size_t gname_len = strlen(gname);
	response_header header = {
			.group = 2,
			.action = 4,
			.content_type = 0,
			.status_code = status_code,
			.content_len = 4 + gname_len,
			.body_len = 4 + gname_len,
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	write_uint32_to_buf(body, admin_id);
	memcpy(body + 4, gname, gname_len);
}

inline void make_response_conv_get_members(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(2, 5, status_code, ls, count, res);
}

inline void make_response_conv_get_list(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(2, 6, status_code, ls, count, res);
}

inline void make_response_chat_create(uint32_t status_code, uint32_t chat_id, char *res)
{
	make_response_uint32(3, 0, status_code, chat_id, res);
}

inline void make_response_chat_delete(uint32_t status_code, char *res)
{
	make_response_empty_body(3, 1, status_code, res);
}

inline void make_response_chat_get_list(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(3, 2, status_code, ls, count, res);
}

inline void make_response_msg_get_all(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(4, 0, status_code, ls, count, res);
}

void make_response_msg_get_detail(uint32_t status, const response_msg *msg, char *res)
{
	response_header header = {
			.group = 4,
			.action = 1,
			.content_type = msg->content_type,
			.status_code = status,
			.content_len = msg->content_length,
			.body_len = 30 + strlen(msg->msg_content),
			// .offset = 0,
			.count = 0};
	make_response_header(&header, res);

	char *body = res + RESPONSE_HEADER_LEN;
	memset(body, 0, RESPONSE_BODY_LEN);

	write_uint32_to_buf(body, msg->msg_id);
	write_uint32_to_buf(body + 4, msg->from_uid);
	write_uint32_to_buf(body + 8, msg->reply_to);
	write_uint32_to_buf(body + 12, msg->conv_id);
	write_uint32_to_buf(body + 16, msg->chat_id);
	write_uint32_to_buf(body + 20, msg->created_at);
	body[24] = (uint8_t)msg->msg_type;
	body[25] = (uint8_t)msg->content_type;
	write_uint32_to_buf(body + 26, msg->content_length);
	memcpy(body + 30, msg->msg_content, strlen(msg->msg_content));
}

inline void make_responses_msg_send(uint32_t status_code, uint32_t msg_id, char *res)
{
	make_response_uint32(4, 2, status_code, msg_id, res);
}

inline void make_response_msg_delete(uint32_t status_code, char *res)
{
	make_response_empty_body(4, 3, status_code, res);
}

inline void make_response_msg_notify_new(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(4, 4, status_code, ls, count, res);
}

inline void make_response_msg_notify_del(uint32_t status_code, uint32_t count, const uint32_t *ls, char *res)
{
	make_response_id_list(4, 5, status_code, ls, count, res);
}
