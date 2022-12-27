#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "int.h"
#include "struct.h"
#include "union.h"

int main()
{
  // common_t *o = create_common_t('2', 1.2, 3.4, 3, 6, 7, 8, 9, 10, 11, 12, "abc", "defffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "ghi", "klm");

  // common_t *o2 = copy_common_t(o);
  // print_common_t(o);
  // char *buff = (char *)calloc(100000, sizeof(char));
  // int out_len = 0;
  // int res = common_t_to_buffer(o, buff, 100000, &out_len);

  // if (res)
  // {
  //   common_t *o3 = calloc(1, sizeof(common_t));
  //   decode_common_t(buff, o3);
  //   print_common_t(o3);
  // }

  com_union_t *o = create_com_union_t_common_t();

  print_com_union_t_common_t(o, "common");

  com_union_t *o2 = create_com_union_t_int();

  print_com_union_t_common_t(o2, "i");

  return 0;
}