#include "./inc/test_common.h"

void test_user_get_info()
{
  // int stt = 0;
  response_user tmp;
  // stt =
  _get_user_info(get_sockfd(), 1, &tmp);
  // PRINT_STATUS_CODE(stt);

  assert(strcmp(tmp.email, "abc@def.com") == 0);
  assert(strcmp(tmp.uname, "user000") == 0);
  assert(strcmp(tmp.phone, "0987654321") == 0);
  SUCCESS("user_test get_user_info pass");
}

void test_user_get_search()
{
  int stt = 0;
  uint32_t *idls = (uint32_t *)malloc(sizeof(uint32_t) * 2048);
  uint32_t idls_len = 0;
  stt = _get_user_search(get_sockfd(), "user00", 0, 10, idls, &idls_len);
  printf("idls_len: %d\n", idls_len);
  for (uint32_t i = 0; i < idls_len; i++)
  {
    printf("User id: %d\n", idls[i]);
  }

  // assert(idls_len > 0);
  assert(stt == 200);
  SUCCESS("user_test get_user_search pass");
}

int main()
{
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  /**
   * Test get_user_info
   */
  test_user_get_info();

  /**
   * Test get_user_search
   */
  test_user_get_search();

  CLOSE_CONN();
  return 0;
}
