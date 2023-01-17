// reset db then run test
// cd build; make; cd ..; make migratedown && make migrateup && ./server

#include "./inc/test_common.h"

uint32_t USER_TABLE_CURRENT_MAX_ID = 0;

int test_register(char *uname, char *email, char *password, char *phone)
{
  request_auth req;
  req.email = email;
  req.uname = uname;
  req.password = password;
  req.phone = phone;

  response_auth res;

  int stt = __register(&req, &res);

  switch (stt)
  {
  case 201:
    // puts("Register success");
    PRINT_USER_ID(res.user_id);
    PRINT_TOKEN(res.token);
    assert(res.user_id == USER_TABLE_CURRENT_MAX_ID + 1);
    USER_TABLE_CURRENT_MAX_ID += 1;
    break;
  case 500:
    // puts("User exists");
    break;
  }

  return stt;
}

int test_login(char *username, char *password)
{
  response_auth res;

  int stt = __login(username, password, &res);

  switch (stt)
  {
  case 200:
    // puts("Login success");
    PRINT_USER_ID(res.user_id);
    PRINT_TOKEN(res.token);
    break;

  default:
    // puts("Login failed");
    break;
  }

  return stt;
}

int main()
{
  atexit(close_conn);

  int sockfd = connect_server("127.0.0.1", 9000);
  if (sockfd == -1)
  {
    printf("Connect to server failed\n");
    return 1;
  }

  DIVIDER();
  assert(test_register("user000", "abc@def.com", "pass", "0987654321") == 201);
  SUCCESS("auth_test register_new_user pass");

  DIVIDER();
  assert(test_register("user001", "abc@def.com", "pass", "0987654321") == 201);
  SUCCESS("auth_test register_new_user pass");

  DIVIDER();
  assert(test_register("user000", "abc@def.com", "pass", "0987654321") == 409);
  SUCCESS("auth_test register_existing_user:409 pass");

  DIVIDER();
  assert(test_login("user000", "pass") == 200);
  SUCCESS("auth_test login pass");

  DIVIDER();
  assert(test_login("user000", "invalidpwd") == 403);
  SUCCESS("auth_test login invalid password:403 pass");

  DIVIDER();
  assert(test_login("user999", "pass") == 404);
  SUCCESS("auth_test login invalid username:404 pass");

  return 0;
}
