#ifndef __TEST_COMMON_H__
#define __TEST_COMMON_H__

#include "all.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <assert.h>
#include <inttypes.h>

#define SUCCESS(msg) printf("\033[0;32m%s\033[0m\n", msg)

#define CONNECT_SERVER()                            \
  {                                                 \
    int sockfd = connect_server("127.0.0.1", 9000); \
    if (sockfd == -1)                               \
    {                                               \
      printf("Connect to server failed\n");         \
      return 1;                                     \
    }                                               \
  }

#define CLOSE_CONN() \
  close_conn();      \
  SUCCESS("after_all close_conn pass");

#define LOGIN_AS_USER_1()                        \
  {                                              \
    int stt = 0;                                 \
    stt = _login("user000", "pass");             \
    PRINT_STATUS_CODE(stt);                      \
    SUCCESS("before_each login_as_user_1 pass"); \
  }

#define LOGOUT()                       \
  {                                    \
    _logout();                         \
    SUCCESS("after_each logout pass"); \
  }

#define LOGIN_AS_USER_2()                        \
  {                                              \
    int stt = 0;                                 \
    stt = _login("user001", "pass");             \
    PRINT_STATUS_CODE(stt);                      \
    SUCCESS("before_each login_as_user_2 pass"); \
  }

#endif
