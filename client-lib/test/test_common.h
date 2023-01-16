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

// using before each test -> do not check status code
#define CREATE_USER_X(x)                                               \
  {                                                                    \
    char user[20] = "user00x";                                         \
    user[6] = x + '0';                                                 \
    request_auth auth = {                                              \
        .uname = user,                                                 \
        .password = "pass",                                            \
        .email = "abc@def.com",                                        \
        .phone = "123456789",                                          \
    };                                                                 \
    _register(&auth);                                                  \
    printf("\033[0;32mbefore_each create_user user_00%d\033[0m\n", x); \
  }

#define LOGIN_AS_USER_X(x)                                          \
  {                                                                 \
    int stt = 0;                                                    \
    char user[20] = "user00x";                                      \
    user[6] = x + '0';                                              \
    stt = _login(user, "pass");                                     \
    assert(stt == 200);                                             \
    printf("\033[0;32mbefore_each login_as user_00%d\033[0m\n", x); \
  }

#define LOGOUT()                       \
  {                                    \
    _logout();                         \
    SUCCESS("after_each logout pass"); \
  }

extern void before_each();

void before_each()
{
  CREATE_USER_X(0);
  CREATE_USER_X(1);
  CREATE_USER_X(2);
  CREATE_USER_X(3);
  CREATE_USER_X(4);
  CREATE_USER_X(5);
  CREATE_USER_X(6);
  CREATE_USER_X(7);
  CREATE_USER_X(8);
  CREATE_USER_X(9);
}

#endif
