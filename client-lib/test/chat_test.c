#include "test_common.h"

int stt;
uint32_t chat_id;
uint32_t idls[2048];
uint32_t idls_len;

void test_chat_create(uint32_t user2_id)
{
  stt = _create_chat(user2_id, &chat_id);

  printf("chat_id: %d\n", chat_id);
}

void test_chat_delete(uint32_t chat_id)
{
  stt = _delete_chat(chat_id);
}

void test_chat_get_list()
{
  stt = _get_chat_list(10, 0, &idls, &idls_len);
}

int main()
{
  CONNECT_SERVER();
  before_each();

  LOGIN_AS_USER_X(1);

  test_chat_create(9);
  assert(stt == 201);
  assert(chat_id > 0);
  SUCCESS("chat_test create_chat pass");

  test_chat_create(9);
  assert(stt == 409);
  assert(chat_id > 0);
  SUCCESS("chat_test create_chat pass");

  // FIXME: user chat with himself?
  // test_chat_create(_get_uid());
  // assert(stt == 400);
  // SUCCESS("chat_test create_chat pass");

  // FIXME: user chat with non-exist user?
  // test_chat_create(1000);
  // assert(stt == 409);
  // SUCCESS("chat_test create_chat pass");

  LOGOUT();
  CLOSE_CONN();

  CONNECT_SERVER();

  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test delete_chat pass");

  LOGIN_AS_USER_X(2);

  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test delete_chat pass");

  LOGOUT();
  CLOSE_CONN();

  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  test_chat_delete(chat_id);
  assert(stt == 200);
  SUCCESS("chat_test delete_chat pass");

  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test delete_chat pass");

  LOGOUT();
  CLOSE_CONN();

  CONNECT_SERVER();

  test_chat_get_list();
  assert(stt == 400);

  LOGIN_AS_USER_X(1);

  // FIXME
  test_chat_get_list(); // todo
  printf("idls_len: %d\n", idls_len);
  assert(stt == 200);

  LOGOUT();
  CLOSE_CONN();

  return 0;
}
