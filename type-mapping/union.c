#include "union.h"
#define ALLOCATE_COM_UNION_T \
  (com_union_t *)calloc(1, sizeof(com_union_t));

com_union_t *create_com_union_t_common_t()
{
  com_union_t *o = ALLOCATE_COM_UNION_T;
  o->common = create_common_t('2', 1.2, 3.4, 3, 6, 7, 8, 9, 10, 11, 12, "abc", "defffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "ghi", "klm");
  return o;
}

com_union_t *create_com_union_t_int()
{
  com_union_t *o = ALLOCATE_COM_UNION_T;
  o->i = 1;
  return o;
}

com_union_t *create_com_union_t_str()
{
  com_union_t *o = ALLOCATE_COM_UNION_T;
  o->str = "Helllooooo";
  return o;
}

void set_com_union_t_common_t(com_union_t *u, common_t *s)
{
  u->common = s;
}

void print_com_union_t_common_t(com_union_t *t, char *type)
{
  if (strcmp(type, "common") == 0)
  {
    print_common_t(t->common);
  }
  else if (strcmp(type, "i") == 0)
  {
    printf("%d", t->i);
  }

  else if (strcmp(type, "str") == 0)
  {
    printf("%s", t->str);
  }

  else
  {
    perror("Invalid type");
  }
}

common_t *com_union_t_get_common_t(com_union_t *in)
{
  return in->common;
}