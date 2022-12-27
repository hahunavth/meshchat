#include "arr.h"

extern char *out_char()
{
  return "Hello";
}
extern void in_char(char *str)
{
  printf(str);
}
extern char *io_char(char *str)
{
  char *buff = (char *)calloc(100, sizeof(char));
  strcpy(buff, "A:");
  strcat(buff, str);
  printf(buff);
  return buff;
}