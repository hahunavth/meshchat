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

uint32_t USER_TABLE_CURRENT_MAX_ID = 0;

int test_register()
{

  request_auth req;
  req.email = "abc@def.com";
  req.uname = "user000";
  req.password = "pass";
  req.phone = "12345678";

  response_auth res;

  int stt = __register(&req, &res);

  PRINT_STATUS_CODE(stt);
  switch (stt)
  {
  case 201:
    puts("Register success");
    PRINT_USER_ID(res.user_id);
    PRINT_TOKEN(res.token);
    assert(res.user_id == USER_TABLE_CURRENT_MAX_ID + 1);
    USER_TABLE_CURRENT_MAX_ID += 1;
    break;
  case 244:
    printf("User exists\n");
    break;
  }

  return stt;
}

int test_login(char *username, char *password)
{
  response_auth res;

  int stt = __login(username, password, &res);

  PRINT_STATUS_CODE(stt);
  switch (stt)
  {
  case 200:
    puts("Login success");
    PRINT_USER_ID(res.user_id);
    PRINT_TOKEN(res.token);
    break;

  default:
    printf("Login failed: %d\n", res.user_id);
    break;
  }
  return stt;
}

int main()
{
  int sockfd = connect_server("127.0.0.1", 9000);
  if (sockfd == -1)
  {
    printf("Connect to server failed\n");
    return 1;
  }
  puts("=====");
  assert(test_register() == 201);
  puts("=====");
  assert(test_register() == 244);
  puts("=====");
  assert(test_login("user000", "pass") == 200);
  puts("=====");
  assert(test_login("user000", "12345678987") == 403);
  puts("=====");
  assert(test_login("user999", "12345678987") == 404);

  /**
   * result: 403, 404 failed
   */

  return 0;
}
