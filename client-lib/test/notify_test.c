#include "./inc/test_common.h"

uint32_t idls[2048];
uint32_t idls_len = 0;
int stt = 0;
uint32_t msg_id;
uint32_t msg2_id;

int main()
{
  CONNECT_SERVER();
  create_fake_user();
  CLOSE_CONN();

  // Tạo chat giữa 2 user
  DIVIDER();
  int chat_id = create_fake_chat(2, 5);

  DIVIDER();
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  // notify new msg -> len = 0
  DIVIDER();
  stt = _notify_new_msg(get_sockfd(), (uint32_t *)&idls, &idls_len);
  assert(stt == 200);
  assert(idls_len == 0);
  SUCCESS("notify_new_msg no new msg:200 pass");

  DIVIDER();
  LOGOUT();
  CLOSE_CONN();
  //
  DIVIDER();
  CONNECT_SERVER();
  LOGIN_AS_USER_X(4);

  // User004 (id=5) gửi tin nhắn cho user001 (id=2)
  stt = _send_msg_text(get_sockfd(), 0, chat_id, 0, "hello world", &msg_id);
  assert(stt == 201);
  SUCCESS("send_msg_text:200 pass");

  stt = _send_msg_text(get_sockfd(), 0, chat_id, 0, "hello world 2", &msg2_id);
  assert(stt == 201);
  SUCCESS("send_msg_text:200 pass");

  DIVIDER();
  LOGOUT();
  CLOSE_CONN();

  DIVIDER();
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  // notify new msg -> len = 1
  DIVIDER();
  stt = _notify_new_msg(get_sockfd(), (uint32_t *)&idls, &idls_len);
  assert(stt == 200);
  assert(idls_len == 2);
  assert(idls[0] == msg2_id);
  assert(idls[1] == msg_id);
  SUCCESS("notify_new_msg has new msg:200 pass");

  DIVIDER();
  stt = _delete_msg(get_sockfd(), msg_id);
  assert(stt == 200);
  SUCCESS("delete_msg:200 pass");

  DIVIDER();
  stt = _notify_del_msg(get_sockfd(), 0, chat_id, (uint32_t *)&idls, &idls_len);
  assert(stt == 200);
  assert(idls_len == 1);
  assert(idls[0] == msg2_id);
  SUCCESS("notify_del_msg:200 pass");

  DIVIDER();
  LOGOUT();
  CLOSE_CONN();
  return 0;
}
