#include "test_common.h"
#include "sql.h"
#include "utils/random.h"
#include "utils/string.h"

#include <assert.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define PRINT_RC(rc) printf("line: %d, rc=%d, msg: %s\n", __LINE__, rc, sqlite3_errstr(rc))

sqlite3* db;

void exit_handler(int signo)
{
	sqlite3_close(db);
	string_clean();
	exit(signo);
}

void test_user_create()
{
	int rc = SQLITE_OK;

	srand(time(NULL));

	char username[10] = {0};
	rand_str(username, 9);
	char password[10] = {0};
	rand_str(password, 9);
	char email[19] = {0};
	rand_email(email, 10);

	user_schema u = {
		.username = username,
		.password = password,
		.email = email,
		.phone_number = "0123456789"		
	};
	uint32_t newuserid = user_create(db, &u, &rc);
	printf("new user id: %u\n", newuserid);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(newuserid > 0);
	SUCCESS("test_user_create passed");

	user_schema* user = user_get_by_id(db, newuserid, &rc);
	PRINT_RC(rc);
	if(user){
		printf("%lu | %s | %s | %s | %s\n", user->id, user->username, user->password, user->phone_number, user->email);
		assert(strcmp(user->username, u.username)==0);
		assert(strcmp(user->password, u.password)==0);
		assert(strcmp(user->phone_number, u.phone_number)==0);
		assert(strcmp(user->email, u.email)==0);
		user_free(user);
		SUCCESS("test_user_get_by_id passed");
	}else{
		switch(rc)
		{
			case SQLITE_EMPTY:
				fprintf(stderr, "not found");
				abort();
			default:
				assert(sql_is_ok(rc));
		}
	}
}

void test_get_user_by_uname()
{
	int rc = SQLITE_OK;

	srand(time(NULL));

	const int ucount = 4;
	const char* usernames[] = {"auser1", "buser2", "cuser3", "duser4"};
	uint32_t ids[ucount];

	char password[10] = {0};
	rand_str(password, 9);
	char email[19] = {0};
	rand_email(email, 10);

	user_schema user = {
		.password = password,
		.phone_number = "0123456789",
		.email = email
	};

	for(int i=0; i<ucount; i++)
	{
		user.username = usernames[i];
		ids[i] = user_create(db, &user, &rc);
		printf("new user id: %u\n", ids[i]);
		PRINT_RC(rc);
		assert(sql_is_ok(rc));
	}

	sllnode_t* list = user_get_by_uname(db, "user", 4, 0, &rc);
	PRINT_RC(rc);

	sllnode_t* iter = list;

	for(int i=0; i<ucount; i++)
	{
		assert((iter->val).l == ids[ucount-i-1]);
		iter = iter->next;
	}
	assert(!iter);
	sll_remove(&list);
	SUCCESS("test_get_user_by_uname passed");

	for(int i=0; i<ucount; i++)
	{
		user_drop(db, ids[i], &rc);
		PRINT_RC(rc);
		assert(sql_is_ok(rc));
	}
	SUCCESS("test_user_drop passed");
}

void test_conv()
{
	/* test conv_create*/
	int rc = SQLITE_OK;
	const char* name = "socket programming";
	uint32_t user_id = 1;

	uint32_t newconv = conv_create(db, user_id, name, &rc);
	printf("newconv = %u\n", newconv);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	SUCCESS("test_conv_create passed");

	int test = conv_is_admin(db, newconv, user_id, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(test);

	SUCCESS("test_conv_is_admin passed");

	test = conv_is_member(db, newconv, user_id, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(test);
	
	SUCCESS("test_conv_is_member passed");

	/* test conv_get_info */
	conv_schema* conv = conv_get_info(db, newconv, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(conv->admin_id == user_id);
	assert(strcmp(conv->name, name) == 0);
	conv_free(conv);

	SUCCESS("test_conv_get_info passed");
	
	/* test conv_join */
	char username[10] = {0};
	rand_str(username, 9);
	char password[10] = {0};
	rand_str(password, 9);
	char email[19] = {0};
	rand_email(email, 10);

	user_schema u = {
		.username = username,
		.password = password,
		.email = email,
		.phone_number = "0123456789"		
	};
	uint32_t newuser = user_create(db, &u, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	conv_join(db, newuser, newconv, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	test = conv_is_member(db, newconv, newuser, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(test);

	SUCCESS("test_conv_join passed");

	/* test conv_get_members */
	sllnode_t* list = conv_get_members(db, newconv, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(((list->val).l == user_id) || ((list->val).l == newuser));
	list = list->next;
	assert(((list->val).l == user_id) || ((list->val).l == newuser));
	assert(!(list->next));
	sll_remove(&list);

	SUCCESS("test_conv_get_members passed");

	/* test conv_quit */
	conv_quit(db, newuser, newconv, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	test = conv_is_member(db, newconv, newuser, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(!test);
	
	SUCCESS("test_conv_quit passed");

	/* test conv_drop */
	conv_drop(db, newconv, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	SUCCESS("test_conv_drop passed");
}

void test_chat()
{
	uint32_t user1 = 1;
	uint32_t user2 = 2;
	int rc = SQLITE_OK;

	uint32_t newchat = chat_create(db, user1, user2, &rc);
	printf("newchat = %u\n", newchat);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	
	SUCCESS("test_chat_create passed");

	uint32_t newchat1 = chat_create(db, user1, user2, &rc);
	printf("newchat = %u\n", newchat1);
	PRINT_RC(rc);
	assert(sql_is_err(rc));

	/* test chat_is_member */
	int test = chat_is_member(db, newchat, user1, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(test);
	test = chat_is_member(db, newchat, user2, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(test);

	SUCCESS("test_chat_is_member passed");

	/* test chat_drop */
	chat_drop(db, newchat, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	SUCCESS("test_chat_drop passed");
}

void test_msg()
{
	int rc = SQLITE_OK;
	const char* name = "socket programming";
	
	uint32_t user1 = 1;
	uint32_t user2 = 2;

	uint32_t newconv = conv_create(db, user1, name, &rc);
	printf("newconv = %u\n", newconv);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	uint32_t newchat = chat_create(db, user1, user2, &rc);
	printf("newchat = %u\n", newchat);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	/* send a message to a conversation */
	msg_schema msg = {
		.from_user_id = user1,
		.reply_to = 0,
		.content = "Hello from user 1",
		.chat_id = 0,
		.conv_id = newconv,
	};
	uint32_t newmsg = msg_send(db, &msg, &rc);
	printf("newmsg = %d\n", newmsg);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	/* test get_msg_detail */
	msg_schema* res = msg_get_detail(db, newmsg, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(msg.from_user_id == res->from_user_id);
	assert(msg.reply_to == res->reply_to);
	assert(strcmp(msg.content, res->content) == 0);
	assert(msg.chat_id == res->chat_id);
	assert(msg.conv_id == res->conv_id);
	assert(res->type == MSG_SENT);
	msg_free(res);

	/* test msg_conv_get_all */
	sllnode_t* list = msg_conv_get_all(db, newconv, 0, 1, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(newmsg == (list->val).l);
	sll_remove(&list);

	SUCCESS("test_msg_conv_get_all passed");

	/* send a message to a chat */
	msg.from_user_id = user2;
	msg.reply_to = newmsg;
	msg.chat_id = newchat;
	msg.conv_id = 0;
	msg.content = "Hello from user 2";
	
	newmsg = msg_send(db, &msg, &rc);
	printf("newmsg = %d\n", newmsg);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));

	SUCCESS("test_msg_send passed");

	/* test msg_chat_get_all */
	list = msg_chat_get_all(db, newchat, 0, 1, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(newmsg == (list->val).l);
	sll_remove(&list);

	SUCCESS("test_msg_chat_get_all passed");

	/* test msg_delete */
	msg_delete(db, newmsg, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	res = msg_get_detail(db, newmsg, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	assert(msg.from_user_id == res->from_user_id);
	assert(msg.reply_to == res->reply_to);
	assert(strlen(res->content) == 0);
	assert(msg.chat_id == res->chat_id);
	assert(msg.conv_id == res->conv_id);
	assert(res->type == MSG_DELETED);
	msg_free(res);

	conv_drop(db, newconv, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
	
	chat_drop(db, newchat, &rc);
	PRINT_RC(rc);
	assert(sql_is_ok(rc));
}

int main(int argc, char** argv)
{	
	(void)argc;
	(void)argv;

	signal(SIGABRT, exit_handler);
	signal(SIGSEGV, exit_handler);
	
	assert(sqlite3_open("../../db/test.db", &db) == SQLITE_OK);

	test_user_create();

	test_get_user_by_uname();

	test_conv();

	test_chat();

	test_msg();

	sqlite3_close(db);

	return 0;
}