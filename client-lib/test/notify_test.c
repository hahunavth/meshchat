#include "./inc/test_common.h"

uint32_t idls[2048];
uint32_t idls_len = 0;
int stt = 0;

int main()
{

  stt = _notify_new_msg(_get_uid(), &idls, &idls_len);
  assert(stt == 200);

  return 0;
}
