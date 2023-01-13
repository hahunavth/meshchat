#include "test_common.h"
#include "response.h"
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
response *res;

void test_response_auth()
{
	make_response_auth_register(201, EMPTY_TOKEN, 1, buf);

	res = response_parse(buf);
	response_header *header = &(res->header);
	response_body *body = res->body;

	assert(header->group == 0);
	assert(header->action == 0);
	assert(header->content_type == 0);
	assert(header->status_code == 201);
	assert(header->content_len == TOKEN_LEN+4);
	assert(header->body_len == TOKEN_LEN+4);
	assert(header->count == 0);

	assert(strcmp((body->r_auth).token, EMPTY_TOKEN) == 0);
	assert((body->r_auth).user_id == 1);

	response_destroy(res);

	SUCCESS("test_make_response_register pass");
	SUCCESS("=> test_make_response_login pass");
}

void test_response_empty_body()
{
	make_response_user_logout(200, buf);

	res = response_parse(buf);
	response_header *header = &(res->header);
	response_body *body = res->body;

	assert(header->group == 1);
	assert(header->action == 0);
	assert(header->content_type == 0);
	assert(header->status_code == 200);
	assert(header->content_len == 0);
	assert(header->body_len == 0);
	assert(header->count == 0);

	response_destroy(res);

	SUCCESS("test_response_body_empty pass");
}

void test_response_body_uint32()
{
	make_response_conv_create(201, 2, buf);

	res = response_parse(buf);
	response_header *header = &(res->header);
	response_body *body = res->body;

	assert(header->group == 2);
	assert(header->action == 0);
	assert(header->content_type == 0);
	assert(header->status_code == 201);
	assert(header->content_len == 4);
	assert(header->body_len == 4);
	assert(header->count == 0);

	assert((body->r_conv).conv_id == 2);

	response_destroy(res);

	SUCCESS("test_response_body_uint32 pass");
}

void test_response_conv_get_info()
{
	const char *gname = "group 51";
	size_t gname_len = strlen(gname);
	make_response_conv_get_info(200, 2, gname, buf);

	res = response_parse(buf);
	response_header *header = &(res->header);
	response_body *body = res->body;

	assert(header->group == 2);
	assert(header->action == 4);
	assert(header->content_type == 0);
	assert(header->status_code == 200);
	assert(header->content_len == 4+gname_len);
	assert(header->body_len == 4+gname_len);
	assert(header->count == 0);

	assert((body->r_conv).admin_id == 2);
	assert(strcmp((body->r_conv).gname, gname) == 0);

	response_destroy(res);

	SUCCESS("test_response_conv_get_info pass");
}

void test_response_get_list()
{
	uint32_t ls[10];
	for(uint32_t i=0; i<10; i++)
		ls[i] = i;
	
	make_response_conv_get_list(200, 10, ls, buf);

	res = response_parse(buf);
	response_header *header = &(res->header);
	response_body *body = res->body;

	assert(header->group == 2);
	assert(header->action == 6);
	assert(header->content_type == 0);
	assert(header->status_code == 200);
	assert(header->content_len == 40);
	assert(header->body_len == 40);
	assert(header->count == 10);

	assert(memcmp((body->r_conv).idls, ls, 40) == 0);

	response_destroy(res);

	SUCCESS("test_response_get_list pass");
}

void test_response_msg_get_detail()
{
	const char *fname = "smile.jpg";
	size_t fsize = 1048576;
	size_t fname_len = strlen(fname);
	response_msg rm = {
		.msg_id = 15, .from_uid = 1, .reply_to = 2,
		.conv_id = 3, .chat_id = 4,
		.content_length = fsize, .content_type = 1,
		.msg_content = fname, .created_at = 12345, .msg_type = 1
	};
	make_response_msg_get_detail(200, &rm, buf);

	res = response_parse(buf);
	response_header *header = &(res->header);
	response_body *body = res->body;

	assert(header->group == 4);
	assert(header->action == 1);
	assert(header->content_type == 1);
	assert(header->status_code == 200);
	assert(header->content_type == 1);
	assert(header->content_len == fsize);
	assert(header->body_len == 30+fname_len);
	assert(header->count == 0);

	assert((body->r_msg).msg_id == 15);
	assert((body->r_msg).from_uid == 1);
	assert((body->r_msg).reply_to == 2);
	assert((body->r_msg).conv_id == 3);
	assert((body->r_msg).chat_id == 4);
	assert((body->r_msg).created_at == 12345);
	assert((body->r_msg).msg_type == 1);
	assert(strcmp((body->r_msg).msg_content, fname) == 0);

	response_destroy(res);

	SUCCESS("test_response_msg_get_detail pass");
}

void abort_handler(int signo)
{
	if (res) response_destroy(res);
	string_clean();
	exit(signo);
}

int main(int argc, char **argv)
{
	(void)argc;
	(void)argv;
	signal(SIGABRT, abort_handler);

	test_response_auth();
	test_response_empty_body();
	test_response_body_uint32();
	test_response_conv_get_info();
	test_response_get_list();
	test_response_msg_get_detail();

	return 0;
}