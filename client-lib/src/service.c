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

static int sockfd = -1;
static char buf[BUFSIZ];

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

/**
 * @brief Send request to server and receive response
 *
 * @param req request: const char *
 * @param res response: char * with _ prefix
 * @return http status code or -1 if error when send or recv
 */
int _register(
    const char *username, const char *password, const char *phone, const char *email,
    char *_token, uint32_t *_user_id)
{
  // send
  make_request_auth_register(username, password, phone, email, buf);
  if (__send(buf) < 0)
    return -1;
  memset(buf, 0, BUFSIZ);

  // recv
  if (__recv(buf) < 0)
    return -1;
  response *res = response_parse(buf);

  printf("%d", res->header.status_code);

  // 201: user created
  if (res->header.status_code == 201)
  {
    strncpy(_token, res->body->r_auth.token, 16);
    *_user_id = res->body->r_auth.user_id;
  }
  // 204: user already exists
  // 400: bad request

  return res->header.status_code;
}

int _login(const char *username, const char *password, char *_token, uint32_t *_user_id)
{
  // send
  make_request_auth_login(username, password, buf);
  if (__send(buf) < 0)
    return -1;
  memset(buf, 0, BUFSIZ);

  // recv
  if (__recv(buf) < 0)
    return -2;
  response *res = response_parse(buf);

  if (res->header.status_code != 200)
  {
    // free_response(res);
    return -3;
  }

  strncpy(_token, res->body->r_auth.token, 16);
  *_user_id = res->body->r_auth.user_id;
  free(res);
  return 0;
}

int _logout(const char *token, const char *user_id,
            int *_status)
{
  // send
  make_request_user_logout(token, user_id, buf);
  if (__send(buf) < 0)
    return -1;
  memset(buf, 0, BUFSIZ);

  // recv
  if (__recv(buf) < 0)
    return -1;
  response *res = response_parse(buf);
  if (res->header.status_code != 200)
  {
    // free_response(res);
    return -1;
  }

  free(res);
  return 0;
}
