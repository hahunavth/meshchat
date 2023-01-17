#include "./inc/test_common.h"

uint32_t chat_1;
uint32_t chat_2;
uint32_t chat_3;
uint32_t chat_4;
uint32_t chat_5;

uint32_t conv_1;
uint32_t conv_2;
uint32_t conv_3;

uint32_t msg_id_1;
uint32_t msg_id_2;
uint32_t msg_id_3;
uint32_t msg_id_4;
uint32_t msg_id_5;
uint32_t msg_id_6;

uint32_t idls[2048];
uint32_t idls_len;

int stt = 0;

int main()
{
  atexit(close_conn);
  // =================================================
  // =================================================
  // TEST CHAT MSG
  // =================================================
  // =================================================

  // =================================================
  // BEFORE ALL TESTS
  // =================================================
  CONNECT_SERVER();
  create_fake_user();
  CLOSE_CONN();

  chat_1 = create_fake_chat(2, 3);
  assert(chat_1 > 0);
  chat_2 = create_fake_chat(2, 4);
  chat_3 = create_fake_chat(2, 5);
  chat_4 = create_fake_chat(2, 5);
  chat_5 = create_fake_chat(3, 6);
  SUCCESS("before_all create_fake_chat pass");

  // // TODO: create fake conv

  // // =================================================
  // // SEND MSG TEXT
  // // =================================================
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  // chat_1
  {
    assert(chat_1 > 0);
    stt = _send_msg_text(0, chat_1, 0, "Hello 2", &msg_id_1);
    assert(stt == 201);
    assert(msg_id_1 > 0);
    SUCCESS("msg_test 1->2 send_msg_text pass");

    stt = _send_msg_text(0, chat_1, 0, "Hello 2", &msg_id_2);
    assert(stt == 201);
    assert(msg_id_2 > 0);
    SUCCESS("msg_test 1->2 send_msg_text pass");

    // chat_2
    assert(chat_2 > 0);
    stt = _send_msg_text(0, chat_2, 0, "Hello 3", &msg_id_3);
    assert(msg_id_1 > 0);
    SUCCESS("msg_test 1->3 send_msg_text pass");

    // chat_4: user2-user5, user 1 not in chat
    stt = _send_msg_text(0, chat_4, 0, "Hello 4", &msg_id_4);
    assert(msg_id_4 == 0);
    assert(stt == 403);
    SUCCESS("msg_test send_msg_text 2<->4 login_as_user1 - 403 pass");
  }

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  // GET ALL MSG
  // =================================================

  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);
  {
    // NOTE: hardcode
    // chat_1 = 1;
    stt = _get_msg_all(10, 0, 0, chat_1, &idls, &idls_len);
    printf("idls_len: %d\n", idls_len);
    for (uint32_t i = 0; i < idls_len; i++)
    {
      printf("Msg id: %d\n", idls[i]);
    }
    assert(stt == 200);
    assert(idls_len == 2);
    assert(idls[0] == msg_id_2);
    assert(idls[1] == msg_id_1);
    SUCCESS("msg_test get_msg_all chat_1 pass");
  }

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  // GET MSG DETAIL
  // =================================================

  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);
  {
    // NOTE: hardcode
    // msg_id_1 = 1;
    response_msg msg;
    assert(msg_id_1 > 0);
    stt = _get_msg_detail(msg_id_1, &msg);
    printf("msg_id: %d\n", msg.msg_id);
    assert(stt == 200);
    assert(msg.msg_id == msg_id_1);
    assert(msg.chat_id == chat_1);
    assert(msg.from_uid == 2);
    // printf("msg_content %d: %s\n", msg.content_length, msg.msg_content);
    assert(strcmp(msg.msg_content, "Hello 2") == 0);
    assert(msg.msg_type == 0);
    SUCCESS("msg_test get_msg_detail msg_id_1 pass");
  }

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  // DELETE MSG
  // =================================================

  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);
  {
    // NOTE: hardcode msg_id_1 = 1
    // msg_id_1 = 1;
    stt = _delete_msg(msg_id_1);
    assert(stt == 200);
    SUCCESS("msg_test delete_msg msg_id_1 pass");
  }

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  // =================================================
  // TEST CONV MSG
  // =================================================
  // =================================================

  // =================================================
  // BEFORE ALL TESTS: CREATE CONV
  // =================================================

  CONNECT_SERVER();
  create_fake_user();

  // user 1 create conv 1
  LOGIN_AS_USER_X(1); // user001 - id 2
  _create_conv("conv_1", &conv_1);
  _join_conv(conv_1, 3); // user002
  _join_conv(conv_1, 4); // user003
  _join_conv(conv_1, 5); // user004

  // user 1 create conv 2
  _create_conv("conv_2", &conv_2);
  _join_conv(conv_2, 6); // user005

  LOGOUT();
  CLOSE_CONN();

  CONNECT_SERVER();
  LOGIN_AS_USER_X(2); // user002 - id 3
  // user 2 create conv 3
  _create_conv("conv_3", &conv_3);

  LOGOUT();
  CLOSE_CONN();

  assert(conv_1 > 0);
  assert(conv_2 > 0);
  assert(conv_3 > 0);
  SUCCESS("before_all create conv pass");

  // =================================================
  // SEND MSG TEXT
  // =================================================
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);
  // user 1 send msg to conv 1
  {
    stt = _send_msg_text(conv_1, 0, 0, "Hello conv 1 p1", &msg_id_1);
    assert(stt == 201);
    assert(msg_id_1 > 0);
    SUCCESS("msg_test 1->2 send_msg_text pass");

    stt = _send_msg_text(conv_1, 0, msg_id_1, "Hello conv 1 p2", &msg_id_2);
    assert(stt == 201);
    assert(msg_id_2 > 0);
    SUCCESS("msg_test 1->2 send_msg_text pass");

    // user001 send msg to conv 1
    stt = _send_msg_text(conv_1, 0, 1000, "Hello conv 1 p3", &msg_id_3);
    assert(stt == 409);
    assert(msg_id_1 > 0);
    SUCCESS("msg_test 1->3 send_msg_text invalid reply_to - 409 pass");

    // user001 send msg to conv 3
    stt = _send_msg_text(conv_3, 0, 0, "Hello conv 3", &msg_id_4);
    assert(msg_id_4 == 0);
    assert(stt == 403);
    SUCCESS("msg_test send_msg_text 2<->4 login_as_user1 - 403 pass");
  }
  LOGOUT();
  CLOSE_CONN();

  // CONNECT_SERVER();
  // LOGIN_AS_USER_X(1);

  return 0;
}
