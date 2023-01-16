#include "common.h"
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
#include "service.h"

#define SWITCH_STT(res)                       \
  PRINT_STATUS_CODE(res->header.status_code); \
  switch (res->header.status_code)

#define FREE_AND_RETURN_STT(res)       \
  int __stt = res->header.status_code; \
  response_destroy(res);               \
  return __stt;

#define UNHANDLE_OTHER_STT_CODE     \
  default:                          \
    perror("Unhandle status code"); \
    break;

// send and recv
static int sockfd = -1;
static char buf[BUFSIZ];
// auth
static uint32_t __uid;
static char __token[TOKEN_LEN];
static int is_auth = 0;

/**
 * @brief Connect to server
 */
int connect_server(const char *addr, uint16_t port)
{
  sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
  if (sockfd < 0)
    return -1;

  // set recv timeout
  struct timeval tv;
  tv.tv_sec = 10;
  tv.tv_usec = 0;
  setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (const char *)&tv, sizeof tv);

  struct sockaddr_in servaddr;
  servaddr.sin_family = AF_INET;
  servaddr.sin_port = htons(port);
  servaddr.sin_addr.s_addr = inet_addr(addr);

  if (connect(sockfd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr_in)) == -1)
  {
    close(sockfd);
    return -1;
  }

  return sockfd;
}

int get_sockfd()
{
  return sockfd;
}

void close_conn()
{
  close(sockfd);
  sockfd = -1;
}

int __send(const char *buff)
{
  if (sockfd == -1)
    return -1;

  int bytes_sent;
  if ((bytes_sent = write(sockfd, buff, BUFSIZ)) < 0)
  {
    close_conn();
    return -1;
  }

  return bytes_sent;
}

int __recv(char *buff)
{
  if (sockfd == -1)
    return -1;

  int bytes_recv;
  if ((bytes_recv = read(sockfd, buff, BUFSIZ)) < 0)
  {
    // free(buff);
    perror("\nError: ");
    close_conn();
    return -1;
  }
  if (bytes_recv == 0)
  {
    // free(buff);
    close_conn();
  }

  return bytes_recv;
}

response *api_call(const char *req)
{
  if (__send(req) < 0)
    return NULL;
  memset(buf, 0, BUFSIZ);

  if (__recv(buf) < 0)
    return NULL;
  response *res = response_parse(buf);

  return res;
}

uint32_t parse_uint32_from_buf(char *buf);

void __auth_cpy(response_auth *dest, const response_auth *src)
{
  memcpy(dest, src, sizeof(response_auth));
  memcpy(dest->token, src->token, TOKEN_LEN);
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
  memset(__token, 0, sizeof(response_auth));
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

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  // 201: user created
  case 201:
    __auth_cpy(_res, &res->body->r_auth);
    break;
    // 204: user already exists
  case 244:
    break;
    // 400: bad request
  case 400:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int __login(const char *username, const char *password, response_auth *_res)
{
  make_request_auth_login(username, password, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    printf("Login success%d", res->body->r_auth.user_id);
    __auth_cpy(_res, &res->body->r_auth);

    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _register(const request_auth *req)
{
  make_request_auth_register(req->uname, req->password, req->phone, req->email, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  // 201: user created
  case 201:
    __set_auth(&res->body->r_auth);
    break;
    // 204: user already exists
  case 244:
    break;
    // 400: bad request
  case 400:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _login(const char *username, const char *password)
{
  make_request_auth_login(username, password, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    __set_auth(&res->body->r_auth);
    break;

    // case 148:
    //   __set_auth(&res->body->r_auth);
    //   // fixme: unknown error
    //   break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _logout(const char *token, const char *user_id)
{
  make_request_user_logout(token, user_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    __clear_auth();
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_user_info(const uint32_t user2_id,
                   response_user *_res)
{
  make_request_user_get_info(__token, __uid, user2_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
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

    // fixme: unknow stt code
  case 147:
    // memcpy(_res, &res->body->r_user, sizeof(request_user));
    // _res->email = calloc(1, strlen(res->body->r_user.email));
    // printf("%s\n\n", res->body->r_user.phone);
    // strcpy(_res->email, res->body->r_user.email);
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_user_search(const char *uname,
                     uint32_t *_idls, int *_len)
{
  make_request_user_search(__token, __uid, uname, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_idls, &res->body->r_user.idls, sizeof(uint32_t) * (res->header.count));
    *_len = res->header.count;
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _create_conv(const char *gname,
                 uint32_t *_gid)
{
  make_request_conv_create(__token, __uid, gname, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 201:
    *_gid = (res->body->r_conv).conv_id;
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _drop_conv(const uint32_t conv_id)
{
  make_request_conv_drop(__token, __uid, conv_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _join_conv(const uint32_t conv_id, const uint32_t user2_id)
{
  make_request_conv_join(__token, __uid, conv_id, user2_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _quit_conv(const uint32_t conv_id)
{
  make_request_conv_quit(__token, __uid, conv_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_conv_info(const uint32_t conv_id,
                   uint32_t *_admin_id, char *_gname)
{
  make_request_conv_get_info(__token, __uid, conv_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    // use admin_id and gname
    *_admin_id = res->body->r_conv.admin_id;
    strcpy(_gname, res->body->r_conv.gname);
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_conv_members(const uint32_t conv_id,
                      uint32_t *_res)
{
  make_request_conv_get_members(__token, __uid, conv_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_res, &res->body->r_conv.idls, sizeof(uint32_t) * (res->header.count));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_conv_list(const int limit, const int offset, uint32_t *_res)
{
  make_request_conv_get_list(__token, __uid, limit, offset, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_res, &res->body->r_conv.idls, sizeof(uint32_t) * (res->header.count));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _create_chat(const uint32_t user2_id, uint32_t *chat_id)
{
  make_request_chat_create(__token, __uid, user2_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 201:
    *chat_id = parse_uint32_from_buf((res->body->r_chat).idls);
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _delete_chat(const uint32_t chat_id)
{
  make_request_chat_delete(__token, __uid, chat_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_chat_list(const int limit, const int offset, uint32_t *_res)
{
  make_request_chat_get_list(__token, __uid, limit, offset, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_res, &res->body->r_chat.idls, sizeof(uint32_t) * (res->header.count));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_msg_all(const int limit, const int offset,
                 const uint32_t conv_id, uint32_t chat_id,
                 uint32_t *_msg_idls)
{
  make_request_msg_get_all(__token, __uid, limit, offset, conv_id, chat_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_msg_idls, &res->body->r_msg.idls, sizeof(uint32_t) * (res->header.count));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _get_msg_detail(const uint32_t msg_id, response_msg *_msg)
{
  make_request_msg_get_detail(__token, __uid, msg_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_msg, &res->body->r_msg, sizeof(response_msg));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _send_msg_text(
    const uint32_t user_id, const uint32_t conv_id,
    const uint32_t chat_id, const uint32_t reply_to, const char *msg,
    uint32_t *_msg_id)
{
  make_requests_msg_send_text(__token, __uid, conv_id, chat_id, reply_to, msg, buf);
  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 201:
    *_msg_id = parse_uint32_from_buf((res->body));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _delete_msg(const uint32_t msg_id)
{
  make_request_msg_delete(__token, __uid, msg_id, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

// ??? conv or chat ??? id ???
int _notify_new_msg(const uint32_t user_id, uint32_t *_idls)
{
  make_request_msg_notify_new(__token, __uid, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_idls, &res->body->r_msg.idls, sizeof(uint32_t) * (res->header.count));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}

int _notify_del_msg(uint32_t *_idls)
{
  make_request_msg_notify_del(__token, __uid, buf);

  response *res = api_call(buf);

  SWITCH_STT(res)
  {
  case 200:
    memcpy(_idls, &res->body->r_conv.idls, sizeof(uint32_t) * (res->header.count));
    break;

    UNHANDLE_OTHER_STT_CODE;
  }

  FREE_AND_RETURN_STT(res);
}
