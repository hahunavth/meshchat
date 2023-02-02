#include "common.h"
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <unistd.h>
#include <assert.h>
#include "service.h"
#include "connection.h"

uint32_t parse_uint32_from_buf(char *buf);

void __auth_cpy(response_auth *dest, const response_auth *src)
{
  memcpy(dest, src, sizeof(response_auth));
  memcpy(dest->token, src->token, TOKEN_LEN);
}

void __parse_uint32_list(char *buf, uint32_t *ls, uint32_t count)
{
  size_t sz = count << 2;
  // uint32_t *ls = (uint32_t *)malloc(sz); // 4xcount
  assert(ls);
  for (uint32_t i = 0; i < count; i++)
  {
    uint32_t cur;
    memcpy(&cur, buf + (i << 2), 4);
    ls[i] = ntohl(cur);
  }
  return ls;
}

void __set_auth(const response_auth *auth)
{
  __uid = auth->user_id;
  memcpy(__token, auth->token, TOKEN_LEN);
  is_auth = 1;
}

void __clear_auth()
{
  __uid = 0;
  memset(__token, 0, TOKEN_LEN);
  is_auth = 0;
}

int get_auth(char *_token, uint32_t *_uid)
{
  if (is_auth)
  {
    memcpy(_token, __token, TOKEN_LEN);
    *_uid = __uid;
    return 1;
  }
  return 0;
}

int is_authenticated()
{
  return is_auth;
}

uint32_t _get_uid() { return __uid; }
char *_get_token() { return __token; }
/**
 * @brief Send request to server and receive response
 *
 * @param req request: const type
 * @param res response: type * with _ prefix
 * @return http status code or -1 if error when send or recv
 */
int __register(const request_auth *req, response_auth *_res)
{
  make_request_auth_register(req->uname, req->password, req->phone, req->email, buf);

  response *res = api_call(__sockfd, buf);

  HANDLE_RES_STT(res)
  {
  // 201: user created
  case 201:
    __auth_cpy(_res, &res->body->r_auth);
    break;
    // 204: user already exists
  case 500:
    break;
    // 400: bad request
  case 400:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int __login(const char *username, const char *password, response_auth *_res)
{
  make_request_auth_login(username, password, buf);

  response *res = api_call(__sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    printf("Login success%d", res->body->r_auth.user_id);
    __auth_cpy(_res, &res->body->r_auth);
    break;
  case 403:
    // invalid pwd
    break;
  case 404:
    // user not found
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _register(const int sockfd, const request_auth *req)
{
  make_request_auth_register(req->uname, req->password, req->phone, req->email, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  // 201: user created
  case 201:
    __set_auth(&res->body->r_auth);
    break;
    // 204: user already exists
  case 409:
    break;
    // 400: bad request
  case 400:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _login(const int sockfd, const char *username, const char *password)
{
  make_request_auth_login(username, password, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 0:
    // error
    break;
  case 200:
    __set_auth(&res->body->r_auth);
    break;
  case 409:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int __logout(const char *token, const char *user_id)
{
  make_request_user_logout(token, user_id, buf);

  response *res = api_call(__sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    // __clear_auth();
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _logout(const int sockfd)
{
  make_request_user_logout(__token, __uid, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    __clear_auth();
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_user_info(const int sockfd, const uint32_t user2_id,
                   response_user *_res)
{
  make_request_user_get_info(__token, __uid, user2_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    // memcpy(_res, &res->body->r_user, sizeof(request_user));
    _res->email = calloc(1, strlen(res->body->r_user.email));
    strcpy(_res->email, res->body->r_user.email);
    _res->phone = calloc(1, strlen(res->body->r_user.phone));
    strcpy(_res->phone, res->body->r_user.phone);
    _res->uname = calloc(1, strlen(res->body->r_user.uname));
    strcpy(_res->uname, res->body->r_user.uname);
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_user_search(const int sockfd, const char *uname, const int32_t offset, int32_t limit,
                     uint32_t *_idls, uint32_t *_len)
{
  make_request_user_search(__token, __uid, uname, limit, offset, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    memcpy(_idls, res->body->r_user.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _create_conv(const int sockfd, const char *gname,
                 uint32_t *_gid)
{
  make_request_conv_create(__token, __uid, gname, buf);
  puts("_create_conv: Make request");
  response *res = api_call(sockfd, buf);
  puts("_create_conv: Call api");
  HANDLE_RES_STT(res)
  {
  case 201:
    *_gid = (res->body->r_conv).conv_id;
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }
  puts("_create_conv: Switch stt");
  FREE_AND_RETURN_STT(res);
}

int _drop_conv(const int sockfd, const uint32_t conv_id)
{
  make_request_conv_drop(__token, __uid, conv_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    break;
  case 403:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _join_conv(const int sockfd, const uint32_t conv_id, const uint32_t user2_id)
{
  make_request_conv_join(__token, __uid, conv_id, user2_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    break;
  case 403:
    break;
  case 500:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _quit_conv(const int sockfd, const uint32_t conv_id, const uint32_t user2_id)
{
  make_request_conv_quit(__token, __uid, conv_id, user2_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    break;
  case 204:
    // admin cannot self-quit
    break;
  case 403:
    // forbidden operation
    break;
    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_conv_info(const int sockfd, const uint32_t conv_id,
                   uint32_t *_admin_id, char *_gname)
{
  make_request_conv_get_info(__token, __uid, conv_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    // use admin_id and gname
    *_admin_id = ((res->body)->r_conv).admin_id;
    strcpy(_gname, ((res->body)->r_conv).gname);
    break;
  case 404:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_conv_members(const int sockfd, const uint32_t conv_id,
                      uint32_t *_res, uint32_t *_len)
{
  make_request_conv_get_members(__token, __uid, conv_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    memcpy(_res, res->body->r_conv.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    printf("%d", res->header.count);
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_conv_list(const int sockfd, const int limit, const int offset, uint32_t *_idls, uint32_t *_len)
{
  make_request_conv_get_list(__token, __uid, limit, offset, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    memcpy(_idls, res->body->r_conv.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _create_chat(const int sockfd, const uint32_t user2_id, uint32_t *_chat_id)
{
  make_request_chat_create(__token, __uid, user2_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 201:
    *_chat_id = (res->body->r_chat).chat_id;
    break;
  case 409:
    // da co chat
    break;
  case 403:
    // khong the chat voi chinh minh
    // chua co quyen
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _delete_chat(const int sockfd, const uint32_t chat_id)
{
  make_request_chat_delete(__token, __uid, chat_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_chat_list(const int sockfd, const int limit, const int offset, uint32_t *_idls, uint32_t *_len)
{
  make_request_chat_get_list(__token, __uid, limit, offset, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    memcpy(_idls, res->body->r_chat.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    break;
  case 400:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_chat_info(const int sockfd, const uint32_t chat_id, uint32_t *_mem1_id, uint32_t *_mem2_id)
{
  make_request_chat_get_info(__token, __uid, chat_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    *_mem1_id = (res->body->r_chat).member_id1;
    *_mem2_id = (res->body->r_chat).member_id2;
    break;
  case 403:
    // not a member
    break;
    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_msg_all(const int sockfd, const int limit, const int offset,
                 const uint32_t conv_id, const uint32_t chat_id,
                 uint32_t *_msg_idls, uint32_t *_len)
{
  make_request_msg_get_all(__token, __uid, limit, offset, conv_id, chat_id, buf);

  response *res = api_call(sockfd, buf);
  printf("res: %d\n", res->header.count);

  HANDLE_RES_STT(res)
  {
  case 200:
    memcpy(_msg_idls, res->body->r_msg.idls, sizeof(uint32_t) * (res->header.count));
    if (_len)
      *_len = res->header.count;
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_msg_detail(const int sockfd, const uint32_t msg_id, response_msg *_msg)
{
  make_request_msg_get_detail(__token, __uid, msg_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    if (!_msg->msg_content)
    {
      _msg->msg_content = malloc(res->body->r_msg.content_length + 1);
    }
    memcpy(_msg->msg_content, res->body->r_msg.msg_content, res->body->r_msg.content_length);
    _msg->msg_id = msg_id;
    _msg->chat_id = res->body->r_msg.chat_id;
    _msg->conv_id = res->body->r_msg.conv_id;
    _msg->msg_id = res->body->r_msg.msg_id;
    _msg->msg_type = res->body->r_msg.msg_type;
    _msg->reply_to = res->body->r_msg.reply_to;
    _msg->content_length = res->body->r_msg.content_length;
    _msg->content_type = res->body->r_msg.content_type;
    _msg->created_at = res->body->r_msg.created_at;
    _msg->from_uid = res->body->r_msg.from_uid;
    break;
  case 404:
    break;
  case 500:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _get_msg_detail_raw(const int sockfd, const uint32_t msg_id,
                        uint32_t *_chat_id, uint32_t *_conv_id, uint32_t *_reply_to,
                        uint32_t *_from_uid, uint32_t *_created_at,
                        uint32_t *_content_type, uint32_t *_content_length,
                        char *_msg_content)
{
  make_request_msg_get_detail(__token, __uid, msg_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    *_chat_id = res->body->r_msg.chat_id;
    *_conv_id = res->body->r_msg.conv_id;
    *_reply_to = res->body->r_msg.reply_to;
    *_from_uid = res->body->r_msg.from_uid;
    *_created_at = res->body->r_msg.created_at;
    *_content_type = res->body->r_msg.content_type;
    *_content_length = res->body->r_msg.content_length;
    memcpy(_msg_content, res->body->r_msg.msg_content, res->body->r_msg.content_length);
    break;
  case 404:
    break;
  case 500:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _send_msg_text(const int sockfd,
                   const uint32_t conv_id,
                   const uint32_t chat_id, const uint32_t reply_to, const char *msg,
                   uint32_t *_msg_id)
{
  make_requests_msg_send_text(__token, __uid, conv_id, chat_id, reply_to, msg, buf);
  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 201:
    *_msg_id = (res->body->r_msg).msg_id;
    break;
  case 403:
    break;
  case 409:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _send_msg_file(
    const int sockfd,
    const uint32_t conv_id,
    const uint32_t chat_id, const uint32_t reply_to, const char *msg,
    uint32_t *_msg_id)
{
  int fd = open(msg, O_RDONLY);
  struct stat sb;
  if (fd < 0)
  {
    perror("open() failed");
    return errno;
  }

  int rc = stat(msg, &sb);
  if (rc < 0)
  {
    perror("stat() failed");
    return errno;
  }

  make_requests_msg_send_file(__token, __uid, conv_id, chat_id, reply_to, sb.st_size, msg, buf);
  if (write(sockfd, buf, BUFSIZ) < 0)
  {
    perror("write() failed");
    return errno;
  }

  ssize_t nbytes;
  while ((nbytes = read(fd, buf, BUFSIZ)) > 0)
  {
    if (write(sockfd, buf, nbytes) != nbytes)
    {
      perror("write() failed");
      close(fd);
      return errno;
    }
  }
  close(fd);

  if ((nbytes = read(sockfd, buf, BUFSIZ)) < 0)
  {
    perror("read() failed");
    return errno;
  }

  if (nbytes == 0)
  {
    puts("Connection closed");
    return 0;
  }

  response *res = response_parse(buf);

  HANDLE_RES_STT(res)
  {
  case 201:
    *_msg_id = (res->body->r_msg).msg_id;
    break;
  case 403:
    break;
  case 409:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _delete_msg(const int sockfd, const uint32_t msg_id)
{
  make_request_msg_delete(__token, __uid, msg_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _notify_new_msg(const int sockfd, const uint32_t user_id, uint32_t *_idls, uint32_t *_len)
{
  make_request_msg_notify_new(__token, __uid, buf);

  response *res = api_call(sockfd, buf);

  if (!res)
  {
    perror("res is null");
  }

  HANDLE_RES_STT(res)
  {
  case 200:
    memcpy(_idls, res->body->r_msg.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    break;
  case 500:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}

int _notify_del_msg(const int sockfd, const uint32_t conv_id, const uint32_t chat_id, uint32_t *_idls, uint32_t *_len)
{
  make_request_msg_notify_del(__token, __uid, conv_id, chat_id, buf);

  response *res = api_call(sockfd, buf);

  HANDLE_RES_STT(res)
  {
  case 200:
    // __parse_uint32_list(res->body, _idls, _len);
    memcpy(_idls, res->body->r_msg.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    break;
  case 500:
    break;

    UNHANDLE_OTHER_STT_CODE(res);
  }

  FREE_AND_RETURN_STT(res);
}
