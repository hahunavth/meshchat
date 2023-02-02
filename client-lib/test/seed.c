#include "./inc/test_common.h"

#define C_HINT "\033[0;30m"
#define C_COLOR_RESET "\033[0m"
#define PRINT_LOCATION \
  printf("Function: %s%s at( %s:%d )%s\n", C_HINT, __func__, __FILE__, __LINE__, C_COLOR_RESET);

#define LOGIN_AS_USER_X2(x)                       \
  {                                               \
    int stt = 0;                                  \
    char uname[1000];                             \
    sprintf(uname, "user_%d", x);                 \
    stt = _login(get_sockfd(), uname, "pass");    \
    assert(stt == 200);                           \
    printf(BLUE "login_as user_00%d\n" RESET, x); \
  }

/**
 * create fake user
 * format: user_x - passx
 */
int create_user_x(int x)
{
  PRINT_LOCATION;
  char uname[1000];
  sprintf(uname, "user_%d", x);

  request_auth auth = {
      .uname = uname,
      .password = "pass",
      .email = "abc@def.com",
      .phone = "123456789",
  };
  return _register(get_sockfd(), &auth);
}

uint32_t send_fake_msg(
    uint32_t conv_id, uint32_t chat_id,
    uint32_t u1id,
    uint32_t repl_id, char *msg)
{
  PRINT_LOCATION;
  uint32_t msg_id = -1;
  CONNECT_SERVER();
  LOGIN_AS_USER_X2(u1id);
  printf("send_fake_msg");
  _send_msg_text(get_sockfd(),
                 conv_id,
                 chat_id,
                 repl_id,
                 msg,
                 &msg_id);
  LOGOUT();
  CLOSE_CONN();
  sleep(1);

  return msg_id;
}

uint32_t create_fake_chat2(uint32_t user_id, uint32_t user2_id)
{
  PRINT_LOCATION;
  CONNECT_SERVER();
  LOGIN_AS_USER_X2(user_id);
  uint32_t chat_id;

  _create_chat(get_sockfd(), user2_id, &chat_id);
  printf("Fake chat created: %d\n", chat_id);

  LOGOUT();
  CLOSE_CONN();
  sleep(1);

  return chat_id;
}

int main()
{
  atexit(close_conn);

  // create user
  {
    CONNECT_SERVER();
    int stt = 0;
    for (int i = 1; i < 100; i++)
    {
      stt = create_user_x(i);
      assert(stt == 201);
    }
    CLOSE_CONN();
  }

  // create chat
  uint32_t chatids[1000];
  {
    chatids[0] = create_fake_chat2(1, 2);
    chatids[1] = create_fake_chat2(1, 3);
    chatids[2] = create_fake_chat2(1, 4);
    chatids[3] = create_fake_chat2(1, 5);
    chatids[4] = create_fake_chat2(2, 5);
    chatids[5] = create_fake_chat2(2, 6);
    chatids[6] = create_fake_chat2(2, 7);
  }

  // create message
  uint32_t msgids[1000];
  {
    msgids[0] = send_fake_msg(0, chatids[0], 1, 0, "Hello 2");
    msgids[1] = send_fake_msg(0, chatids[0], 2, 0, "Hello 1");
    msgids[2] = send_fake_msg(0, chatids[0], 1, msgids[1], "Hi 2");
    msgids[3] = send_fake_msg(0, chatids[0], 2, 0, "Hi 1");
  }
}
