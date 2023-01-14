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

#define USER_TABLE_CURRENT_MAX_ID 0

int test_register()
{
  char username[32] = "user000";

  char token[17];
  uint32_t user_id = -1;

  int res = _register(username, "pass", "12345678", "abc@def.com", token, &user_id);

  if (res == 0)
  {
    puts("Register success");
    PRINT_USER_ID(user_id);
    PRINT_TOKEN(token);

    assert(user_id == USER_TABLE_CURRENT_MAX_ID + 1);

    return 0;
  }
  else
  {
    printf("Register failed\n");
    return 1;
  }
}

int test_login()
{
  char username[32] = "user000";

  char token[17];
  uint32_t user_id;

  int res = _login(username, "pass", token, &user_id);

  if (res == 0)
  {
    puts("Login success");
    PRINT_USER_ID(user_id);
    PRINT_TOKEN(token);

    assert(user_id == USER_TABLE_CURRENT_MAX_ID + 1);

    return 0;
  }
  else
  {
    printf("Login failed: %d\n", res);
    return 1;
  }
}

int main()
{
  int sockfd = connect_server("127.0.0.1", 9000);
  if (sockfd == -1)
  {
    printf("Connect to server failed\n");
    return 1;
  }

  assert(test_register() == 0);
  assert(test_login() == 0);

  return 0;
}
