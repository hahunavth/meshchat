#ifndef __TM_STRUCT_H__
#define __TM_STRUCT_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CREATE_COMMON_T_PARAMS \
  (                            \
      char c,                  \
      float f,                 \
      double d,                \
      int8_t int8,             \
      int16_t int16,           \
      int32_t int32,           \
      int64_t int64,           \
      u_int8_t u_int8,         \
      u_int16_t u_int16,       \
      u_int32_t u_int32,       \
      u_int64_t u_int64,       \
      char *str1,              \
      char *str2,              \
      char *str3,              \
      char *str4)

typedef struct common_t
{
  char c;
  float f;
  double d;
  int8_t int8;
  int16_t int16;
  int32_t int32;
  int64_t int64;
  u_int8_t u_int8;
  u_int16_t u_int16;
  u_int32_t u_int32;
  u_int64_t u_int64;
  char str1[8192];
  char str2[8192];
  char *str3;
  char *str4;
} common_t;

extern common_t *copy_common_t(common_t *in);
extern common_t *create_common_t CREATE_COMMON_T_PARAMS;

extern int common_t_to_buffer(common_t *in, char *buff, int buff_len, int *len_out);
extern char *c_common_t_to_buffer(common_t *in, int *len_out);

extern common_t *decode_common_t(char *buff, common_t *out);
extern common_t *c_decode_common_t(char *buff);

extern void print_common_t(common_t *in);
#endif