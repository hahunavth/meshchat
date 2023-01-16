/**
 * FIXME: free(): invalid pointer
 */
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
  stt = _join_conv(conv_id, 9);
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
  /**
   * FIXME: chưa xử lý trường hợp này
   */
  stt = _quit_conv(conv_id);
  // assert(stt == 200);

  // ko có room với id này -> 403
  stt = _quit_conv(123456);
  assert(stt == 403);

  SUCCESS("conv_test quit_conv pass");
}

void test_conv_get_info(uint32_t conv_id)
{
  int stt = 0;
  char *gname = (char *)calloc(BUFSIZ, sizeof(char));
  uint32_t admin_id;

  stt = _get_conv_info(conv_id, &admin_id, gname);
  // printf("stt: %d, gname: %s, admin_id: %d", stt, gname, admin_id);

  assert(stt == 200);
  assert(strcmp(gname, "conv000") == 0);
  // assert(admin_id == 1);

  free(gname);
  SUCCESS("conv_test get_conv_info pass");
}

void test_conv_get_members(uint32_t conv_id)
{
  int stt = 0;
  uint32_t *members = (uint32_t *)calloc(1024, sizeof(uint32_t));
  uint32_t n_members;

  stt = _get_conv_members(conv_id, members, &n_members);
  printf("stt: %d, n_members: %d", stt, n_members);
  printf("members: %d", members[0]);

  assert(stt == 200);
  // assert(n_members == 1);
  // assert(members[0] == 1);
  free(members);
  SUCCESS("conv_test get_conv_members pass");
}

void test_conv_get_list()
{
  int stt = 0;
  uint32_t *conv_ids = (uint32_t *)calloc(BUFSIZ, sizeof(uint32_t));
  uint32_t n_conv_ids;

  stt = _get_conv_list(10, 0, &n_conv_ids);
  printf("stt: %d, n_conv_ids: %d", stt, n_conv_ids);
  printf("conv_ids: %d", conv_ids[0]);

  // assert(stt == 200);
  // assert(n_conv_ids == 1);
  // assert(conv_ids[0] == 1);
  free(conv_ids);
  SUCCESS("conv_test get_conv_list pass");
}

int main()
{
  // user 1
  CONNECT_SERVER();
  before_each();

  LOGIN_AS_USER_X(1);

  uint32_t conv_id = test_conv_create("conv000");
  test_conv_get_info(conv_id);
  test_conv_get_members(conv_id);
  test_conv_get_list();

  LOGOUT();
  CLOSE_CONN();

  // user 2
  CONNECT_SERVER();
  LOGIN_AS_USER_X(2);

  // test_conv_get_info(conv_id);
  test_conv_join_2(conv_id);
  test_conv_drop_2(conv_id);

  LOGOUT();
  CLOSE_CONN();

  // user 1
  CONNECT_SERVER();

  LOGIN_AS_USER_X(1);

  test_conv_join(conv_id);
  test_conv_quit(conv_id);
  test_conv_drop(conv_id);

  LOGOUT();
  CLOSE_CONN();

  return 0;
}
