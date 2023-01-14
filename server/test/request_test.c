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

#define TOKEN_LEN		16
#define EMPTY_TOKEN		"\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06\x06"

char buf[BUFSIZ];
request *req;

void test_request_auth()
{
	const char *uname = "user123";
	const char *pass = "Pass@1234";
	const char *phone = "0123456789";
	const char *email = "myemail@mail.com";
	size_t sum_len = strlen(uname) + strlen(pass) + strlen(phone) + strlen(email);

	make_request_auth_register(uname, pass, phone, email, buf);

	req = request_parse(buf);
	request_header *header = &(req->header);
	request_body *body = req->body;

	assert(header->group == 0);
	assert(header->action == 0);
	assert(header->content_type == 0);
	assert(header->content_len == sum_len + 3);
	assert(header->body_len == sum_len + 3);
	// assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, TOKEN_LEN) == 0);
	assert(header->user_id == 0);
	assert(header->limit == -1);
	assert(header->offset == -1);

	request_auth ra = body->r_auth;
	assert(strcmp(ra.uname, uname) == 0);
	assert(strcmp(ra.password, pass) == 0);
	assert(strcmp(ra.phone, phone) == 0);
	assert(strcmp(ra.email, email) == 0);

	request_destroy(req);

	SUCCESS("test_request_auth_register pass");

	make_request_auth_login(uname, pass, buf);

	req = request_parse(buf);
	header = &(req->header);
	body = req->body;
	sum_len = strlen(uname) + strlen(pass);

	assert(header->group == 0);
	assert(header->action == 1);
	assert(header->content_type == 0);
	assert(header->content_len == sum_len + 1);
	assert(header->body_len == sum_len + 1);
	// assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, TOKEN_LEN) == 0);
	assert(header->user_id == 0);
	assert(header->limit == -1);
	assert(header->offset == -1);

	ra = body->r_auth;
	assert(strcmp(ra.uname, uname) == 0);
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
	assert(header->content_type == 0);
	assert(header->content_len == 0);
	assert(header->body_len == 0);
	// assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, TOKEN_LEN) == 0);
	assert(header->user_id == 1);
	assert(header->limit == 10);
	assert(header->offset == 3);

	request_destroy(req);

	SUCCESS("test_make_request_conv_get_list pass");
	SUCCESS("=> test_make_request_chat_get_list pass");
	SUCCESS("=> test_request_msg_notify_new pass");
	SUCCESS("=> test_request_msg_notify_del pass");
	SUCCESS("=> test_request_user_logout pass");
}

void test_request_body_uint32()
{
	make_request_conv_join(EMPTY_TOKEN, 1, 10, 2, buf);

	req = request_parse(buf);
	request_header *header = &(req->header);
	request_body *body = req->body;

	assert(header->group == 2);
	assert(header->action == 2);
	assert(header->content_type == 0);
	assert(header->content_len == 4);
	assert(header->body_len == 4);
	// assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, TOKEN_LEN) == 0);
	assert(header->user_id == 1);
	assert(header->limit == -1);
	assert(header->offset == -1);

	assert((body->r_conv).conv_id == 10);
	assert((body->r_conv).user_id == 2);

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

	const char *msg = "Hello world";
	uint32_t msg_len = strlen(msg);
	const char* fname = "smile.jpg";
	uint32_t fsize = 1048576;

	make_requests_msg_send_text(EMPTY_TOKEN, 1, conv_id, chat_id, reply_to, msg, buf);
	
	req = request_parse(buf);
	request_header *header = &(req->header);
	request_body *body = req->body;

	assert(header->group == 4);
	assert(header->action == 2);
	assert(header->content_type == 0);
	assert(header->content_len == 12+msg_len);
	assert(header->body_len == 12+msg_len);
	// assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, TOKEN_LEN) == 0);
	assert(header->user_id == 1);
	assert(header->limit == -1);
	assert(header->offset == -1);

	assert((body->r_msg).conv_id == conv_id);
	assert((body->r_msg).chat_id == chat_id);
	assert((body->r_msg).reply_id == reply_to);
	assert(strcmp((body->r_msg).msg_content, msg) == 0);
	
	request_destroy(req);

	SUCCESS("test_make_requests_msg_send_text pass");


	make_requests_msg_send_file(EMPTY_TOKEN, 1, conv_id, chat_id, reply_to, fsize, fname, buf);
	
	req = request_parse(buf);
	header = &(req->header);
	body = req->body;

	assert(header->group == 4);
	assert(header->action == 2);
	assert(header->content_type == 1);
	assert(header->content_len == fsize);
	assert(header->body_len == 12+strlen(fname));
	// assert(header->offset0 == 0);
	assert(memcmp(header->token, EMPTY_TOKEN, TOKEN_LEN) == 0);
	assert(header->user_id == 1);
	assert(header->limit == -1);
	assert(header->offset == -1);

	assert((body->r_msg).conv_id == conv_id);
	assert((body->r_msg).chat_id == chat_id);
	assert((body->r_msg).reply_id == reply_to);
	assert(strcmp((body->r_msg).msg_content, fname) == 0);

	request_destroy(req);

	SUCCESS("test_make_requests_msg_send_msg pass");

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
	test_request_msg_send();

	return 0;
}