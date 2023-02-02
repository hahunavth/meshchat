#ifndef __CLIENT_LIB_INC_CONNECTION_H__
#define __CLIENT_LIB_INC_CONNECTION_H__

#include "common.h"
#include "errno.h"
#include "utils.h"

/**
 * Handle res exception and handle each status
 * if res NULL: send, recv error
 * else: exception
 */
#define HANDLE_RES_STT(res)                   \
  if (res == NULL)                            \
    switch (errno)                            \
    {                                         \
    case EWOULDBLOCK:                         \
      clearerr(stderr);                       \
      return 408;                             \
    default:                                  \
      return -1;                              \
    }                                         \
  PRINT_STATUS_CODE(res->header.status_code); \
  switch (res->header.status_code)

#define FREE_AND_RETURN_STT(res)       \
  int __stt = res->header.status_code; \
  response_destroy(res);               \
  return __stt;

#define UNHANDLE_OTHER_STT_CODE(res)                                                 \
  default:                                                                           \
  {                                                                                  \
    char tmp[BUFSIZ];                                                                \
    sprintf(tmp, MAGENTA "Unhandle status code: %d" RESET, res->header.status_code); \
    perror(tmp);                                                                     \
  }                                                                                  \
  break;

// send and recv
static int __sockfd = -1;
static char buf[BUFSIZ];

#endif
