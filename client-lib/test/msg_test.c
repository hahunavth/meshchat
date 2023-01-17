#include "test_common.h"

int main()
{
  int stt = 0;
  // =================================================
  // BEFORE ALL TESTS
  // =================================================
  CONNECT_SERVER();
  create_fake_user();
  CLOSE_CONN();

  uint32_t chat_1;
  uint32_t chat_2;
  uint32_t chat_3;
  uint32_t chat_4;
  uint32_t chat_5;
  chat_1 = create_fake_chat(1, 2);
  assert(chat_1 > 0);
  chat_2 = create_fake_chat(1, 3);
  chat_3 = create_fake_chat(1, 4);
  chat_4 = create_fake_chat(2, 5);
  chat_5 = create_fake_chat(3, 6);
  SUCCESS("before_all create_fake_chat pass");

  // =================================================
  // SEND MSG TEXT
  // =================================================
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  uint32_t msg_id_1;
  uint32_t msg_id_2;
  uint32_t msg_id_3;
  uint32_t msg_id_4;
  uint32_t msg_id_5;
  uint32_t msg_id_6;

  // chat_1
  assert(chat_1 > 0);
  stt = _send_msg_text(0, chat_1, 0, "Hello 2", &msg_id_1);
  assert(stt == 201);
  assert(msg_id_1 > 0);
  SUCCESS("msg_test 1->2 send_msg_text pass");

  // stt = _send_msg_text(-1, chat_1, -1, "Hello 2", &msg_id_2);
  // assert(stt == 201);
  // assert(msg_id_2 > 0);
  // SUCCESS("msg_test 1->2 send_msg_text pass");

  // // chat_2
  // assert(chat_2 > 0);
  // stt = _send_msg_text(-1, chat_2, -1, "Hello 3", &msg_id_3);
  // assert(msg_id_1 > 0);
  // SUCCESS("msg_test 1->3 send_msg_text pass");

  // // chat_4: user2-user5, user 1 not in chat
  // stt = _send_msg_text(-1, chat_4, -1, "Hello 4", &msg_id_4);
  // assert(msg_id_4 > 0);
  // assert(stt == 403);
  // SUCCESS("msg_test send_msg_text 2<->4 login_as_user1 - 403 pass");

  LOGOUT();
  CLOSE_CONN();
  return 0;
}
