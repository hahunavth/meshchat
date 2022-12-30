#include "test_common.h"
#include "request.h"
#include "utils/string.h"

#include <arpa/inet.h>
#include <assert.h>
#include <signal.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define EMPTY_TOKEN "\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06"

char buf[BUFSIZ];
request *req;

void test_request_auth()
{
	const char *username = "user123";
	const char *pass = "Pass@1234";
	const char *phone = "0123456789";
	const char *email = "myemail@mail.com";
	size_t sum_len = strlen(username) + strlen(pass) + strlen(phone) + strlen(email);

	make_request_auth_register(username, pass, phone, email, buf);

	req = request_parse(buf);
	request_header *header = &(req->header);
	request_body *body = req->body;

	assert(header->group == 0);
	assert(header->action == 0);
	assert(header->multipart == 0);
	assert(header->content_len == sum_len + 3);
	assert(header->body_len == sum_len + 3);
	assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, 16) == 0);
	assert(header->user_id == 0);
	assert(header->limit == -1);
	assert(header->offset1 == -1);

	request_auth ra = body->r_auth;
	assert(strcmp(ra.username, username) == 0);
	assert(strcmp(ra.password, pass) == 0);
	assert(strcmp(ra.phone, phone) == 0);
	assert(strcmp(ra.email, email) == 0);

	request_destroy(req);

	SUCCESS("test_request_auth_register pass");

	make_request_auth_login(username, pass, buf);

	req = request_parse(buf);
	header = &(req->header);
	body = req->body;
	sum_len = strlen(username) + strlen(pass);

	assert(header->group == 0);
	assert(header->action == 1);
	assert(header->multipart == 0);
	assert(header->content_len == sum_len + 1);
	assert(header->body_len == sum_len + 1);
	assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, 16) == 0);
	assert(header->user_id == 0);
	assert(header->limit == -1);
	assert(header->offset1 == -1);

	ra = body->r_auth;
	assert(strcmp(ra.username, username) == 0);
	assert(strcmp(ra.password, pass) == 0);

	request_destroy(req);

	SUCCESS("test_request_auth_login pass");
}

void test_request_user_id_only()
{
	make_request_conv_get_list(EMPTY_TOKEN, 1, 10, 3, buf);

	req = request_parse(buf);
	request_header *header = &(req->header);
	// request_body* body = req->body;

	assert(header->group == 2);
	assert(header->action == 6);
	assert(header->multipart == 0);
	assert(header->content_len == 0);
	assert(header->body_len == 0);
	assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, 16) == 0);
	assert(header->user_id == 1);
	assert(header->limit == 10);
	assert(header->offset1 == 3);

	request_destroy(req);

	SUCCESS("test_make_request_conv_get_list pass");
	SUCCESS("=> test_make_request_chat_get_list pass");
	SUCCESS("=> test_request_msg_notify_new pass");
	SUCCESS("=> test_request_msg_notify_del pass");
	SUCCESS("=> test_request_user_logout pass");
}

void test_request_body_uint32()
{
	make_request_conv_join(EMPTY_TOKEN, 1, 10, buf);

	req = request_parse(buf);
	request_header *header = &(req->header);
	request_body *body = req->body;

	assert(header->group == 2);
	assert(header->action == 2);
	assert(header->multipart == 0);
	assert(header->content_len == 4);
	assert(header->body_len == 4);
	assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, 16) == 0);
	assert(header->user_id == 1);
	assert(header->limit == -1);
	assert(header->offset1 == -1);

	assert((body->r_conv).conv_id == 10);

	request_destroy(req);

	SUCCESS("test_make_request_conv_join pass");
	SUCCESS("=> test_make_request_conv_drop pass");
	SUCCESS("=> test_make_request_conv_quit pass");
	SUCCESS("=> test_make_request_conv_get_info pass");
	SUCCESS("=> test_make_request_conv_get_info pass");
	SUCCESS("=> test_make_request_conv_get_members pass");
	SUCCESS("=> test_make_request_chat_create pass");
	SUCCESS("=> test_make_request_chat_drop pass");
	SUCCESS("=> test_make_request_msg_delete pass");
}

void test_request_msg_send()
{
	uint32_t conv_id = 16;
	uint32_t chat_id = 17;
	uint32_t reply_to = 18;

	char msg[(REQUEST_MSG_MAX_LEN << 1)];
	memset(msg, 'A', REQUEST_MSG_MAX_LEN);
	memset(msg + REQUEST_MSG_MAX_LEN, 'B', REQUEST_MSG_MAX_LEN);

	char *iter = msg;

	uint32_t total_len = (REQUEST_MSG_MAX_LEN << 1);
	assert(make_requests_msg_send(EMPTY_TOKEN, total_len, 1, conv_id, chat_id, reply_to, msg, &iter, buf));
	req = request_parse(buf);
	request_header *header = &(req->header);
	request_body *body = req->body;
	assert(header->group == 4);
	assert(header->action == 2);
	assert(header->multipart == 1);
	assert(header->content_len == total_len+24);
	assert(header->body_len == REQUEST_BODY_LEN);
	assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, 16) == 0);
	assert(header->user_id == 1);
	assert(header->limit == -1);
	assert(header->offset1 == -1);
	assert(memcmp((body->r_msg).msg_content, msg, REQUEST_MSG_MAX_LEN) == 0);
	request_destroy(req);

	assert(make_requests_msg_send(EMPTY_TOKEN, total_len, 1, conv_id, chat_id, reply_to, msg, &iter, buf));
	assert(memcmp(buf+REQUEST_HEADER_LEN+12, msg+REQUEST_MSG_MAX_LEN, REQUEST_MSG_MAX_LEN) == 0);

	SUCCESS("test_make_requests_msg_send pass");

}

void abort_handler(int signo)
{
	if (req)
		request_destroy(req);
	string_clean();
	exit(signo);
}

int main(int argc, char **argv)
{
	(void)argc;
	(void)argv;
	signal(SIGABRT, abort_handler);

	test_request_auth();
	test_request_user_id_only();
	test_request_body_uint32();
	test_request_msg();

	return 0;
}