#include "request.h"
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

uint32_t parse_uint32_from_buf(const char *buf)
{
	uint32_t num;
	memcpy(&num, buf, 4);
	return ntohl(num);
}

void write_uint32_to_buf(char *buf, uint32_t num)
{
	num = htonl(num);
	memcpy(buf, &num, 4);
}

void request_parse_auth(request *req, const char *body);
void request_parse_user(request *req, const char *body);
void request_parse_conv(request *req, const char *body);
void request_parse_chat(request *req, const char *body);
void request_parse_msg(request *req, const char *body);

void request_body_destroy(request_body *body, int8_t group);

request *request_parse(const char *buf)
{
	request *req = (request *)calloc(1, sizeof(request));
	assert(req);

	request_header *header = &(req->header);
	req->body = NULL;

	header->group = buf[0];
	header->action = buf[1];
	header->content_type = buf[2];
	header->content_len = parse_uint32_from_buf(buf + 4);
	header->body_len = parse_uint32_from_buf(buf + 8);
	// header->offset0 = parse_uint32_from_buf(buf+12);

	header->token = string_mem_n(buf + 16, TOKEN_LEN);

	header->user_id = parse_uint32_from_buf(buf + 32);
	header->limit = parse_uint32_from_buf(buf + 36);
	header->offset = parse_uint32_from_buf(buf + 40);

	switch (header->group)
	{
	case 0:
		request_parse_auth(req, buf + REQUEST_HEADER_LEN);
		break;
	case 1:
		request_parse_user(req, buf + REQUEST_HEADER_LEN);
		break;
	case 2:
		request_parse_conv(req, buf + REQUEST_HEADER_LEN);
		break;
	case 3:
		request_parse_chat(req, buf + REQUEST_HEADER_LEN);
		break;
	case 4:
		request_parse_msg(req, buf + REQUEST_HEADER_LEN);
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
	for (; ((*buf) != '\0') && ((buf - old) <= REQUEST_BODY_LEN); buf++)
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

void request_parse_auth(request *req, const char *body)
{
	request_header *header = &(req->header);

	request_body *rb = (request_body *)calloc(1, sizeof(request_body));
	assert(rb);

	uint32_t bodylen = header->body_len;
	char _body[bodylen + 1];
	memcpy(_body, body, bodylen);
	_body[bodylen] = '\0';

	char *rest = _body;
	char *uname = field_tokenizer(rest, &rest);
	char *password = field_tokenizer(rest, &rest);
	char *phone, *email;

	(rb->r_auth).uname = string_new(uname);
	(rb->r_auth).password = string_new(password);
	(rb->r_auth).phone = NULL;
	(rb->r_auth).email = NULL;

	if (header->action == 0)
	{
		phone = field_tokenizer(rest, &rest);
		email = field_tokenizer(rest, &rest);
		int email_len = header->body_len - strlen(uname) - strlen(password) - strlen(phone) - 3;
		if (email_len < 0)
		{
			request_body_destroy(rb, header->group);
			return;
		}
		email[email_len] = '\0';

		(rb->r_auth).phone = string_new(phone);
		(rb->r_auth).email = string_new(email);
	}
	else if (header->action != 1)
	{
		request_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void request_parse_user(request *req, const char *body)
{
	request_header *header = &(req->header);

	request_body *rb = (request_body *)calloc(1, sizeof(request_body));
	assert(rb);

	(rb->r_user).user_id = 0;
	(rb->r_user).uname = NULL;

	switch (header->action)
	{
	case 0:
		break;
	case 1:
		(rb->r_user).user_id = parse_uint32_from_buf(body);
		break;
	case 2:
		(rb->r_user).uname = string_new_n(body, header->body_len);
		break;
	default:
		request_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void request_parse_conv(request *req, const char *body)
{
	request_header *header = &(req->header);

	request_body *rb = (request_body *)calloc(1, sizeof(request_body));
	assert(rb);

	switch (header->action)
	{
	case 0:
		(rb->r_conv).gname = string_new_n(body, header->body_len);
		break;
	case 1:
	case 3:
	case 4:
	case 5:
		(rb->r_conv).conv_id = parse_uint32_from_buf(body);
		break;
	case 2:
		(rb->r_conv).conv_id = parse_uint32_from_buf(body);
		(rb->r_conv).user_id = parse_uint32_from_buf(body + 4);
		break;
	case 6:
		break;
	default:
		request_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void request_parse_chat(request *req, const char *body)
{
	request_header *header = &(req->header);

	request_body *rb = (request_body *)calloc(1, sizeof(request_body));
	assert(rb);

	switch (header->action)
	{
	case 0:
		(rb->r_chat).user_id2 = parse_uint32_from_buf(body);
		break;
	case 1:
		(rb->r_chat).chat_id = parse_uint32_from_buf(body);
		break;
	case 2:
		break;
	default:
		request_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void request_parse_msg(request *req, const char *body)
{
	request_header *header = &(req->header);

	request_body *rb = (request_body *)calloc(1, sizeof(request_body));
	assert(rb);

	switch (header->action)
	{
	case 0:
	case 5:
		(rb->r_msg).conv_id = parse_uint32_from_buf(body);
		(rb->r_msg).chat_id = parse_uint32_from_buf(body + 4);
		break;
	case 1:
	case 3:
		(rb->r_msg).msg_id = parse_uint32_from_buf(body);
		;
		break;
	case 2:
		(rb->r_msg).conv_id = parse_uint32_from_buf(body);
		(rb->r_msg).chat_id = parse_uint32_from_buf(body + 4);
		(rb->r_msg).reply_id = parse_uint32_from_buf(body + 8);
		(rb->r_msg).msg_content = string_new_n(body + 12, header->body_len - 12);
		break;
	case 4:
		break;
	default:
		request_body_destroy(rb, header->group);
		return;
	}

	req->body = rb;
}

void request_body_destroy(request_body *body, int8_t group)
{
	if (!body)
		return;
	switch (group)
	{
	case 0:
	{
		request_auth ra = body->r_auth;
		if (ra.uname)
			string_remove(ra.uname);
		if (ra.password)
			string_remove(ra.password);
		if (ra.phone)
			string_remove(ra.phone);
		if (ra.email)
			string_remove(ra.email);
	}
	break;
	case 1:
	{
		request_user ru = body->r_user;
		if (ru.uname)
			string_remove(ru.uname);
	}
	break;
	case 2:
	{
		request_conv rc = body->r_conv;
		if (rc.gname)
			string_remove(rc.gname);
	}
	break;
	case 3:
		break;
	case 4:
	{
		request_msg rm = body->r_msg;
		if (rm.msg_content)
			string_remove(rm.msg_content);
	}
	}
	free(body);
}

void request_destroy(request *req)
{
	if (!req)
		return;
	request_body_destroy(req->body, (req->header).group);
	free(req);
}

//////////////////////////////////////////////////

static void make_request_header(request_header *header, char *res)
{
	memset(res, 0, REQUEST_HEADER_LEN);

	res[0] = header->group;
	res[1] = header->action;
	res[2] = header->content_type;

	write_uint32_to_buf(res + 4, header->content_len);
	write_uint32_to_buf(res + 8, header->body_len);
	// write_uint32_to_buf(res+12, header->offset0);

	memcpy(res + 16, header->token, TOKEN_LEN);

	write_uint32_to_buf(res + 32, header->user_id);
	write_uint32_to_buf(res + 36, header->limit);
	write_uint32_to_buf(res + 40, header->offset);
}

static void make_request_string(const char *token, uint8_t group, uint8_t action, uint32_t user_id, const char *str, char *res)
{
	size_t len = strlen(str);
	request_header header = {
			.group = group,
			.action = action,
			.content_type = 0,
			.content_len = len,
			.body_len = len,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	memcpy(body, str, len);
}

static void make_request_uint32(const char *token, uint8_t group, uint8_t action, uint32_t user_id, uint32_t num, char *res)
{
	request_header header = {
			.group = group,
			.action = action,
			.content_type = 0,
			.content_len = 4,
			.body_len = 4,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	write_uint32_to_buf(body, num);
}

static void make_request_page(const char *token, uint8_t group, uint8_t action, uint32_t user_id, int32_t limit, int32_t offset, char *res)
{
	request_header header = {
			.group = group,
			.action = action,
			.content_type = 0,
			.content_len = 0,
			.body_len = 0,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = limit,
			.offset = offset};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
}

void make_request_auth_register(const char *uname, const char *password, const char *phone, const char *email, char *res)
{
	size_t uname_len = strlen(uname);
	size_t pass_len = strlen(password);
	size_t phone_len = strlen(phone);
	size_t email_len = strlen(email);
	size_t sum_len = uname_len + pass_len + phone_len + email_len;

	request_header header = {
			.group = 0,
			.action = 0,
			.content_type = 0,
			.content_len = sum_len + 3,
			.body_len = sum_len + 3,
			// .offset0 = 0,
			.token = EMPTY_TOKEN,
			.user_id = 0,
			.limit = -1,
			.offset = -1};

	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	memcpy(body, uname, uname_len);
	body[uname_len] = BODY_DEMLIMITER;
	body += (uname_len + 1);
	memcpy(body, password, pass_len);
	body[pass_len] = BODY_DEMLIMITER;
	body += (pass_len + 1);
	memcpy(body, phone, phone_len);
	body[phone_len] = BODY_DEMLIMITER;
	body += (phone_len + 1);
	memcpy(body, email, email_len);
}

void make_request_auth_login(const char *uname, const char *password, char *res)
{
	size_t uname_len = strlen(uname);
	size_t pass_len = strlen(password);
	size_t sum_len = uname_len + pass_len;

	request_header header = {
			.group = 0,
			.action = 1,
			.content_type = 0,
			.content_len = sum_len + 1,
			.body_len = sum_len + 1,
			// .offset0 = 0,
			.token = EMPTY_TOKEN,
			.user_id = 0,
			.limit = -1,
			.offset = -1};

	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	memcpy(body, uname, uname_len);
	body[uname_len] = BODY_DEMLIMITER;
	body += (uname_len + 1);
	memcpy(body, password, pass_len);
	body[pass_len] = BODY_DEMLIMITER;
	body += (pass_len + 1);
}

void make_request_user_logout(const char *token, uint32_t user_id, char *res)
{
	request_header header = {
			.group = 1,
			.action = 0,
			.content_type = 0,
			.content_len = 0,
			.body_len = 0,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);
}

inline void make_request_user_get_info(const char *token, uint32_t user_id, uint32_t user_id2, char *res)
{
	make_request_uint32(token, 1, 1, user_id, user_id2, res);
}

void make_request_user_search(const char *token, uint32_t user_id, const char *uname, int32_t limit, int32_t offset, char *res)
{
	size_t len = strlen(uname);
	request_header header = {
			.group = 1,
			.action = 2,
			.content_type = 0,
			.content_len = len,
			.body_len = len,
			.token = token,
			.user_id = user_id,
			.limit = limit,
			.offset = offset};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	memcpy(body, uname, len);
}

inline void make_request_conv_create(const char *token, uint32_t user_id, const char *gname, char *res)
{
	make_request_string(token, 2, 0, user_id, gname, res);
}

inline void make_request_conv_drop(const char *token, uint32_t user_id, uint32_t conv_id, char *res)
{
	make_request_uint32(token, 2, 1, user_id, conv_id, res);
}

void make_request_conv_join(const char *token, uint32_t user_id, uint32_t conv_id, uint32_t user_id2, char *res)
{
	make_request_uint32(token, 2, 2, user_id, conv_id, res);
	write_uint32_to_buf(res + REQUEST_HEADER_LEN + 4, user_id2);
}

inline void make_request_conv_quit(const char *token, uint32_t user_id, uint32_t conv_id, char *res)
{
	make_request_uint32(token, 2, 3, user_id, conv_id, res);
}

inline void make_request_conv_get_info(const char *token, uint32_t user_id, uint32_t conv_id, char *res)
{
	make_request_uint32(token, 2, 4, user_id, conv_id, res);
}

inline void make_request_conv_get_members(const char *token, uint32_t user_id, uint32_t conv_id, char *res)
{
	make_request_uint32(token, 2, 5, user_id, conv_id, res);
}

inline void make_request_conv_get_list(const char *token, uint32_t user_id, int32_t limit, int32_t offset, char *res)
{
	make_request_page(token, 2, 6, user_id, limit, offset, res);
}

inline void make_request_chat_create(const char *token, uint32_t user_id, uint32_t user_id2, char *res)
{
	make_request_uint32(token, 3, 0, user_id, user_id2, res);
}

inline void make_request_chat_delete(const char *token, uint32_t user_id, uint32_t chat_id, char *res)
{
	make_request_uint32(token, 3, 1, user_id, chat_id, res);
}

inline void make_request_chat_get_list(const char *token, uint32_t user_id, int32_t limit, int32_t offset, char *res)
{
	make_request_page(token, 3, 2, user_id, limit, offset, res);
}

void make_request_msg_get_all(const char *token, uint32_t user_id, int32_t limit, int32_t offset, uint32_t conv_id, uint32_t chat_id, char *res)
{
	request_header header = {
			.group = 4,
			.action = 0,
			.content_type = 0,
			.content_len = 8,
			.body_len = 8,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = limit,
			.offset = offset};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	write_uint32_to_buf(body, conv_id);
	write_uint32_to_buf(body + 4, chat_id);
}

inline void make_request_msg_get_detail(const char *token, uint32_t user_id, uint32_t msg_id, char *res)
{
	make_request_uint32(token, 4, 1, user_id, msg_id, res);
}

void make_requests_msg_send_text(const char *token, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, uint32_t reply_to, const char *msg, char *res)
{
	size_t msg_len = strlen(msg);
	request_header header = {
			.group = 4,
			.action = 2,
			.content_type = 0,
			.content_len = 12 + msg_len,
			.body_len = 12 + msg_len,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	write_uint32_to_buf(body, conv_id);
	write_uint32_to_buf(body + 4, chat_id);
	write_uint32_to_buf(body + 8, reply_to);
	memcpy(body + 12, msg, msg_len);
}

void make_requests_msg_send_file(const char *token, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, uint32_t reply_to, uint32_t fsize, const char *fname, char *res)
{
	size_t fname_len = strlen(fname);
	request_header header = {
			.group = 4,
			.action = 2,
			.content_type = 1,
			.content_len = fsize,
			.body_len = 12 + fname_len,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	write_uint32_to_buf(body, conv_id);
	write_uint32_to_buf(body + 4, chat_id);
	write_uint32_to_buf(body + 8, reply_to);
	memcpy(body + 12, fname, fname_len);
}

inline void make_request_msg_delete(const char *token, uint32_t user_id, uint32_t msg_id, char *res)
{
	make_request_uint32(token, 4, 3, user_id, msg_id, res);
}

void make_request_msg_notify_new(const char *token, uint32_t user_id, char *res)
{
	request_header header = {
			.group = 4,
			.action = 4,
			.content_type = 0,
			.content_len = 0,
			.body_len = 0,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);
}

void make_request_msg_notify_del(const char *token, uint32_t user_id, uint32_t conv_id, uint32_t chat_id, char *res)
{
	request_header header = {
			.group = 4,
			.action = 5,
			.content_type = 0,
			.content_len = 0,
			.body_len = 0,
			// .offset0 = 0,
			.token = token,
			.user_id = user_id,
			.limit = -1,
			.offset = -1};
	make_request_header(&header, res);

	char *body = res + REQUEST_HEADER_LEN;
	memset(body, 0, REQUEST_BODY_LEN);

	write_uint32_to_buf(body, conv_id);
	write_uint32_to_buf(body + 4, chat_id);
}
