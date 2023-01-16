#include "test_common.h"

uint32_t test_conv_create(char *name)
{
  int stt = 0;
  uint32_t conv_id = 0;
  stt = _create_conv(name, &conv_id);
  // PRINT_STATUS_CODE(stt);
  printf("conv_id: %d\n", conv_id);
  assert(stt == 201);
  assert(conv_id > 0);
  SUCCESS("conv_test create_conv pass");
  return conv_id;
}

void test_conv_join(uint32_t conv_id)
{
  int stt = 0;
  stt = _join_conv(conv_id, _get_uid());
  PRINT_STATUS_CODE(stt);
  assert(stt == 500);
  SUCCESS("conv_test join_conv pass");
}

void test_conv_drop(uint32_t conv_id)
{
  int stt = 0;
  stt = _drop_conv(conv_id);
  PRINT_STATUS_CODE(stt);
  assert(stt == 200);
  SUCCESS("conv_test drop_conv pass");
}

void test_conv_join_2(uint32_t conv_id)
{
  int stt = 0;
  stt = _join_conv(conv_id, 2);
  PRINT_STATUS_CODE(stt);
  assert(stt == 200);
  SUCCESS("conv_test join_conv pass");
}

int main()
{
  CONNECT_AND_LOGIN();

  uint32_t created = test_conv_create("conv000");
  test_conv_join(created);
  test_conv_drop(created);
  test_conv_join_2(created);

  return 0;
}
