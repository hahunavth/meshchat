#ifndef __TM_UNION_H__
#define __TM_UNION_H__
#include "struct.h"

typedef union com_union_t
{
  common_t *common;
  char *str;
  int8_t i;
} com_union_t;

extern com_union_t *create_com_union_t_common_t();
extern com_union_t *create_com_union_t_int();

extern com_union_t *create_com_union_t_str();
extern void set_com_union_t_common_t(com_union_t *u, common_t *s);
extern void print_com_union_t_common_t(com_union_t *t, char *type);

extern common_t *com_union_t_get_common_t(com_union_t *in);

#endif
