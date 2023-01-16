#include "test_common.h"

uint32_t test_conv_create(char *name)
{
  int stt = 0;
  uint32_t conv_id = 0;
  stt = _create_conv(name, &conv_id);

  printf("conv_id: %d\n", conv_id);

  assert(stt == 201);
  assert(conv_id > 0);
  SUCCESS("conv_test create_conv pass");
  return conv_id;
}

void test_conv_drop(uint32_t conv_id)
{
  int stt = 0;

  // admin drop room mình tạo -> 200
  stt = _drop_conv(conv_id);
  assert(stt == 200);

  // ko có room với id này -> 403
  stt = _drop_conv(conv_id);
  assert(stt == 403);

  SUCCESS("conv_test drop_conv pass");
}

void test_conv_drop_2(uint32_t conv_id)
{
  int stt = 0;

  // drop room do ng khac tao -> 403
  stt = _drop_conv(conv_id);
  assert(stt == 403);

  SUCCESS("conv_test drop_conv pass");
}

void test_conv_join(uint32_t conv_id)
{
  int stt = 0;
  // admin cho ng mới vào room mình tạo -> 200
  stt = _join_conv(conv_id, 2);
  assert(stt == 200);

  // admin join vào room mình tạo -> 500
  stt = _join_conv(conv_id, _get_uid());
  assert(stt == 500);
  SUCCESS("conv_test join_conv pass");

  // admin cho ng có trong room vào room mình tạo -> 500
  stt = _join_conv(conv_id, 2);
  assert(stt == 500);
  SUCCESS("conv_test join_conv pass");
}

void test_conv_join_2(uint32_t conv_id)
{
  int stt = 0;

  // ng ko phai admin join vao room
  stt = _join_conv(conv_id, 2);
  assert(stt == 403);
  SUCCESS("conv_test join_conv pass");
}

void test_conv_quit(uint32_t conv_id)
{
  int stt = 0;

  // admin quit room mình tạo -> err
  stt = _quit_conv(conv_id);
  assert(stt == 200);

  // ko có room với id này -> 403
  // stt = _quit_conv(123456);
  // assert(stt == 403);

  SUCCESS("conv_test quit_conv pass");
}

int main()
{
  // user 1
  CONNECT_SERVER();
  LOGIN_AS_USER_1();

  uint32_t conv_id = test_conv_create("conv000");

  LOGOUT();
  CLOSE_CONN();

  // user 2
  CONNECT_SERVER();
  LOGIN_AS_USER_2();

  test_conv_join_2(conv_id);
  test_conv_drop_2(conv_id);

  LOGOUT();
  CLOSE_CONN();

  // user 1
  CONNECT_SERVER();
  LOGIN_AS_USER_1();

  test_conv_join(conv_id);
  test_conv_quit(conv_id);
  // test_conv_drop(conv_id);

  LOGOUT();
  CLOSE_CONN();

  return 0;
}
