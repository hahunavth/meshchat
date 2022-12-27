#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "struct.h"

int find_c(char *s, char c)
{
  for (int i = 0; 1; i++)
  {
    if (s[i] == c)
    {
      return i;
    }
  }
  // return -1;
}

#define SEPARATOR ':'; // Tạm để dấu ':' thay cho 0x03 vì có thể in ra đc

#define CPY_ATTR(BUFF, ATTR, COUNT) \
  {                                 \
    int i = sizeof(*ATTR);          \
    memcpy(BUFF + COUNT, ATTR, i);  \
    COUNT += i;                     \
  }

#define CPY_ARR(BUFF, ATTR, SIZE, COUNT) \
  {                                      \
    memcpy(BUFF + COUNT, ATTR, SIZE);    \
    COUNT += SIZE;                       \
    BUFF[COUNT] = SEPARATOR;             \
    COUNT += 1;                          \
  }

#define RESTORE_ATTR(BUFF, ATTR, COUNT) \
  {                                     \
    int i = sizeof(*ATTR);              \
    memcpy(ATTR, BUFF + COUNT, i);      \
    COUNT += i;                         \
  }

#define RESTORE_ARR(BUFF, ATTR, COUNT)        \
  {                                           \
    char sperator = SEPARATOR;                \
    int i = find_c((BUFF + COUNT), sperator); \
    memcpy(ATTR, BUFF + COUNT, i);            \
    COUNT += i + 1;                           \
  }

#define ALLOCATE_AND_RESTORE_ARR(BUFF, p_ATTR, COUNT) \
  {                                                   \
    char sperator = SEPARATOR;                        \
    int i = find_c((BUFF + COUNT), sperator);         \
    *p_ATTR = (char *)calloc(i, 1);                   \
    memcpy(*p_ATTR, BUFF + COUNT, i);                 \
    COUNT += i + 1;                                   \
  }

common_t *copy_common_t(common_t *in)
{
  common_t *obj = (common_t *)calloc(1, sizeof(common_t));
  obj->c = in->c;
  obj->f = in->f;
  obj->d = in->d;

  obj->int8 = in->int8;
  obj->int16 = in->int16;
  obj->int32 = in->int32;
  obj->int64 = in->int64;
  obj->u_int8 = in->u_int8;
  obj->u_int16 = in->u_int16;
  obj->u_int32 = in->u_int32;
  obj->u_int64 = in->u_int64;

  strcpy(obj->str1, in->str1);
  strcpy(obj->str2, in->str2);
  obj->str3 = (char *)calloc(strlen(in->str3), sizeof(char));
  obj->str4 = (char *)calloc(strlen(in->str4), sizeof(char));
  strcpy(obj->str3, in->str3);
  strcpy(obj->str4, in->str4);

  return obj;
}

common_t *create_common_t(
    char c,
    float f,
    double d,
    int8_t int8,
    int16_t int16,
    int32_t int32,
    int64_t int64,
    u_int8_t u_int8,
    u_int16_t u_int16,
    u_int32_t u_int32,
    u_int64_t u_int64,
    char *str1,
    char *str2,
    char *str3,
    char *str4)
{
  common_t *obj = (common_t *)calloc(1, sizeof(common_t));
  obj->c = c;
  obj->f = f;
  obj->d = d;

  obj->int8 = int8;
  obj->int16 = int16;
  obj->int32 = int32;
  obj->int64 = int64;
  obj->u_int8 = u_int8;
  obj->u_int16 = u_int16;
  obj->u_int32 = u_int32;
  obj->u_int64 = u_int64;

  strcpy(obj->str1, str1);
  strcpy(obj->str2, str2);
  obj->str3 = (char *)calloc(strlen(str3), sizeof(char));
  obj->str4 = (char *)calloc(strlen(str4), sizeof(char));
  strcpy(obj->str3, str3);
  strcpy(obj->str4, str4);

  return obj;
}

int calc_enc_buffer_len_common_t(const common_t *in)
{
  return sizeof(common_t) + (strlen(in->str3) + 1) + (strlen(in->str4) + 1) - (2 * sizeof(char *));
}

int __common_t_to_buffer(common_t *in, char *buff, int buff_len, int calc_sz, int *len_out)
{
  if (buff_len < calc_sz)
  {
    perror("Convert failed");
    return 0;
  }

  int count = 0;

  CPY_ATTR(buff, &(in->c), count);
  CPY_ATTR(buff, &(in->f), count);
  CPY_ATTR(buff, &(in->d), count);

  CPY_ATTR(buff, &(in->int8), count);
  CPY_ATTR(buff, &(in->int16), count);
  CPY_ATTR(buff, &(in->int32), count);
  CPY_ATTR(buff, &(in->int64), count);

  CPY_ATTR(buff, &(in->u_int8), count);
  CPY_ATTR(buff, &(in->u_int16), count);
  CPY_ATTR(buff, &(in->u_int32), count);
  CPY_ATTR(buff, &(in->u_int64), count);

  CPY_ARR(buff, in->str1, strlen(in->str1), count);
  CPY_ARR(buff, in->str2, strlen(in->str2), count);
  CPY_ARR(buff, in->str3, strlen(in->str3), count);
  CPY_ARR(buff, in->str4, strlen(in->str4), count);

  if (len_out)
    *len_out = count;
  return 1;
}

int common_t_to_buffer(common_t *in, char *buff, int buff_len, int *len_out)
{
  int sz = calc_enc_buffer_len_common_t(in);
  return __common_t_to_buffer(in, buff, buff_len, sz, len_out);
}
char *c_common_t_to_buffer(common_t *in, int *len_out)
{
  int sz = calc_enc_buffer_len_common_t(in);
  char *buff = (char *)calloc(sz, sizeof(char));
  return buff;
}

common_t *decode_common_t(char *buff, common_t *out)
{
  int count = 0;

  RESTORE_ATTR(buff, &(out->c), count);
  RESTORE_ATTR(buff, &(out->f), count);
  RESTORE_ATTR(buff, &(out->d), count);

  RESTORE_ATTR(buff, &(out->int8), count);
  RESTORE_ATTR(buff, &(out->int16), count);
  RESTORE_ATTR(buff, &(out->int32), count);
  RESTORE_ATTR(buff, &(out->int64), count);

  RESTORE_ATTR(buff, &(out->u_int8), count);
  RESTORE_ATTR(buff, &(out->u_int16), count);
  RESTORE_ATTR(buff, &(out->u_int32), count);
  RESTORE_ATTR(buff, &(out->u_int64), count);

  RESTORE_ARR(buff, out->str1, count);
  RESTORE_ARR(buff, out->str2, count);
  ALLOCATE_AND_RESTORE_ARR(buff, &(out->str3), count);
  ALLOCATE_AND_RESTORE_ARR(buff, &(out->str4), count);

  return out;
}

common_t *c_decode_common_t(char *buff)
{
  common_t *out = (common_t *)calloc(1, sizeof(common_t));
  return decode_common_t(buff, out);
}

void print_common_t(common_t *in)
{
  printf("  {\n");

  printf("\tc: %c\n", in->c);
  printf("\tf: %f\n", in->f);
  printf("\td: %f\n", in->d);

  printf("\tint8: %d", in->int8);
  printf("\tint16: %d", in->int16);
  printf("\tint32: %d", in->int32);
  printf("\tint64: %ld", in->int64);

  printf("\n");

  printf("\tu_int8: %d", in->u_int8);
  printf("\tu_int16: %d", in->u_int16);
  printf("\tu_int32: %d", in->u_int32);
  printf("\tu_int64: %ld", in->u_int64);

  printf("\n");

  printf("\tstr1: %s\n", in->str1);
  printf("\tstr2: %s\n", in->str2);
  printf("\tstr3: %s\n", in->str3);
  printf("\tstr4: %s\n", in->str4);

  printf("  }\n");
}