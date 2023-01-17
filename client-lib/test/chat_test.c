#include "./inc/test_common.h"

int stt;
uint32_t chat_id;
uint32_t idls[2048];
uint32_t idls_len;

void test_chat_create(uint32_t user2_id)
{
  stt = _create_chat(user2_id, &chat_id);

  printf("chat_id: %d\n", chat_id);
}

void test_chat_delete(uint32_t chat_id)
{
  stt = _delete_chat(chat_id);
}

void test_chat_get_list()
{
  stt = _get_chat_list(10, 0, &idls, &idls_len);
  printf("stt: %d, idls_len: %d\n", stt, idls_len);
  for (uint32_t i = 0; i < idls_len; i++)
  {
    printf("- idls[%d]: %d\n", i, idls[i]);
  }
}

int main()
{
  // kết nối đến server
  CONNECT_SERVER();
  create_fake_user(); // tạo ng dùng fake có tên từ user000 đến user009

  LOGIN_AS_USER_X(1); // Đăng nhập với user001

  test_chat_create(9);
  assert(stt == 201);
  assert(chat_id > 0);
  SUCCESS("chat_test tạo chat vs ng dùng mới - 200 pass");

  test_chat_create(9);
  assert(stt == 409);
  assert(chat_id > 0);
  SUCCESS("chat_test đã có chat từ trước - 409 pass");

  // FIXME: user chat with himself?
  test_chat_create(_get_uid());
  assert(stt == 400);
  SUCCESS("chat_test chat vs chính mình - 40x pass");

  // FIXME: user chat with non-exist user?
  test_chat_create(1000);
  assert(stt == 409);
  SUCCESS("chat_test chat vs user2_id ko tồn tại pass");

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  CONNECT_SERVER();

  test_chat_get_list();
  assert(stt == 403);
  SUCCESS("chat_test lấy danh sách chat của user khi chưa đăng nhập - 403 pass");

  LOGIN_AS_USER_X(1); // login user001

  // FIXME
  test_chat_get_list(); // todo
  printf("idls_len: %d\n", idls_len);
  assert(stt == 200);
  SUCCESS("chat_test lấy danh sách chat của user khi đã đăng nhập - 200 pass");

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  CONNECT_SERVER();
  LOGIN_AS_USER_X(1);

  test_chat_delete(chat_id);
  assert(stt == 200);
  SUCCESS("chat_test xóa chat mà user có quyền xóa đã đăng nhập - 200 pass");

  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test xóa chat không tồn tại - 403 pass");

  LOGOUT();
  CLOSE_CONN();

  // =================================================
  CONNECT_SERVER();

  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test xóa chat khi chưa đăng nhập - 403 pass");

  LOGIN_AS_USER_X(2);

  test_chat_delete(chat_id);
  assert(stt == 403);
  SUCCESS("chat_test xóa chat khi đăng nhập vs ng dùng ko trong đoạn chat - 403 pass");

  LOGOUT();
  CLOSE_CONN();

  return 0;
}
