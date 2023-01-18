#include "./inc/test_common.h"

int stt;
uint32_t chat_id;
uint32_t idls[2048];
uint32_t idls_len;

void test_chat_create(uint32_t user2_id)
{
  stt = _create_chat(get_sockfd(), user2_id, &chat_id);

  printf("chat_id: %d\n", chat_id);
}

void test_chat_delete(uint32_t chat_id)
{
  stt = _delete_chat(get_sockfd(), chat_id);
}

void test_chat_get_list()
{
  stt = _get_chat_list(get_sockfd(), 10, 0, &idls, &idls_len);
  printf("stt: %d, idls_len: %d\n", stt, idls_len);
  for (uint32_t i = 0; i < idls_len; i++)
  {
    printf("- idls[%d]: %d\n", i, idls[i]);
  }
}

int main()
{
  CONNECT_SERVER();
  create_fake_user();

  LOGIN_AS_USER_X(1);

  DIVIDER();
  test_chat_create(9);
  assert(stt == 201);
  assert(chat_id > 0);
  SUCCESS("chat_test create_chat pass");

  DIVIDER();
  test_chat_create(9);
  assert(stt == 409);
  assert(chat_id > 0);
  SUCCESS("chat_test create_chat exists_before:409 pass");

  DIVIDER();
  test_chat_create(_get_uid());
  assert(stt == 403);
  SUCCESS("chat_test create_chat_with him/herself - 403 pass");

  DIVIDER();
  test_chat_create(1000);
  assert(stt == 409);
  SUCCESS("chat_test create_chat user2_id_not_exists:409 pass");

  LOGOUT();
  CLOSE_CONN();
  DIVIDER();
  // =================================================
  CONNECT_SERVER();

  DIVIDER();
  test_chat_get_list();
  assert(stt == 403);
  SUCCESS("chat_test get_chat_list no_perm:403 pass");

  LOGIN_AS_USER_X(1); // login user001

  DIVIDER();
  test_chat_get_list(); // todo
  printf("idls_len: %d\n", idls_len);
  assert(stt == 200);
  SUCCESS("chat_test get_chat logined pass");

  LOGOUT();
  CLOSE_CONN();
  DIVIDER();

  // =================================================
  DIVIDER();
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  DIVIDER();
  test_chat_delete(chat_id);
  assert(stt == 200);
  SUCCESS("chat_test delete_chat logined+has_perm pass");

  DIVIDER();
  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test delete_chat not_exists:403 pass");

  LOGOUT();
  CLOSE_CONN();
  DIVIDER();

  // =================================================
  DIVIDER();
  CONNECT_SERVER();

  DIVIDER();
  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test delete_chat no_login:403 pass");

  DIVIDER();
  LOGIN_AS_USER_X(2);

  DIVIDER();
  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test delete_chat user_not_in_chat:403 pass");

  DIVIDER();
  LOGOUT();
  CLOSE_CONN();

  return 0;
}
