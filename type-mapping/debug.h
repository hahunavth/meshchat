#ifndef __DEBUG_H__
#define __DEBUG_H__

#include <string.h>
#include <unistd.h>
#include <syscall.h>
#include "color.h"

#define THREAD_ID syscall(SYS_gettid)

#define PRINT_LOCATION \
  printf("\t%sat( %s:%d )%s\n", C_HINT, __FILE__, __LINE__, C_COLOR_RESET);

#define PRINT_INT(n)                                           \
  printf("> Int: %s%s%s=%s%d%s %sat( %s:%d )%s\n",             \
         C_BLUE, #n, C_COLOR_RESET, C_GREEN, n, C_COLOR_RESET, \
         C_HINT, __FILE__, __LINE__, C_COLOR_RESET);

#define PRINT_LONG(n) \
  printf("> Long: %s%s%s=%s%ld%s %sat( %s:%d )%s\n", C_BLUE, #n, C_COLOR_RESET, C_GREEN, n, C_COLOR_RESET, C_HINT, __FILE__, __LINE__, C_COLOR_RESET);

#define PRINT_STR(s)                                                                                                                                    \
  CATCH_NULL_PTR(s);                                                                                                                                    \
  printf(">[%s%ld%s] Str: %s%s%s[%ld]=%s \"%s\"%s %sat( %s:%d )%s\n",                                                                                   \
         C_BLUE, THREAD_ID, C_COLOR_RESET, C_BLUE, #s, C_COLOR_RESET, strlen(s), C_GREEN, s, C_COLOR_RESET, C_HINT, __FILE__, __LINE__, C_COLOR_RESET); \
  // printf("%ld", );

#define DELAY_LOCATION(I)  \
  sleep(I);                \
  printf("Wait: %dms", I); \
  PRINT_LOCATION;

#endif
