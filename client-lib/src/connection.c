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
#include <assert.h>
#include <errno.h>
#include "connection.h"

#define HANDLE_SOCKET_ERRNO_AND_RETURN_NULL        \
  switch (errno)                                   \
  {                                                \
  case EWOULDBLOCK:                                \
    perror(RED "Error: timeout" RESET);            \
    return NULL;                                   \
  case ECONNRESET:                                 \
    perror(RED "Error: conn reset by peer" RESET); \
    return NULL;                                   \
  case EMSGSIZE:                                   \
    perror(RED "Error: message too long" RESET);   \
    return NULL;                                   \
  default:                                         \
    perror(RED "Error: failed" RESET);             \
    return NULL;                                   \
  }

/**
 * @brief Connect to server
 */
int connect_server(const char *addr, uint16_t port)
{
  __sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
  if (__sockfd < 0)
    return -1;

  // set recv timeout
  struct timeval tv;
  tv.tv_sec = 10;
  tv.tv_usec = 0;
  setsockopt(__sockfd, SOL_SOCKET, SO_RCVTIMEO, (const char *)&tv, sizeof tv);

  struct sockaddr_in servaddr;
  servaddr.sin_family = AF_INET;
  servaddr.sin_port = htons(port);
  servaddr.sin_addr.s_addr = inet_addr(addr);

  if (connect(__sockfd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr_in)) == -1)
  {
    close(__sockfd);
    __sockfd = -1;
    return -1;
  }

  return __sockfd;
}

int get_sockfd()
{
  return __sockfd;
}

void close_conn()
{
  printf(YELLOW "Closing connection...\n" RESET);
  fflush(stdout);
  close(__sockfd);
  __sockfd = -1;
}

int __send(const int sockfd, const char *buff)
{
  if (sockfd == -1)
    return -1;

  int bytes_sent = send(sockfd, buff, BUFSIZ, 0);
  if ((bytes_sent) < 0)
  {
    close_conn();
    return -1;
  }
  if (bytes_sent == 0)
  {
    close_conn();
    return -1;
  }

  return bytes_sent;
}

int __recv(char *buff)
{
  if (__sockfd == -1)
    return -1;

  int bytes_recv;
  if ((bytes_recv = recv(__sockfd, buff, BUFSIZ, 0)) < 0)
  {
    // free(buff);
    perror("\nError: ");
    close_conn();
    return -1;
  }
  if (bytes_recv == 0)
  {
    printf(YELLOW "Connection closed\n" RESET);
    close_conn();
  }

  return bytes_recv;
}

void ___send(const char *buff)
{
  printf("___send: %d\n", __send(__sockfd, buff));
}

response *api_call(const int sockfd, const char *req)
{
  int sz;

  if (req == NULL)
    perror("API_CALL:ERROR: Request is NULL\n");

  sz = send(sockfd, req, BUFSIZ, 0);
  if ((sz) < 0)
  {
    HANDLE_SOCKET_ERRNO_AND_RETURN_NULL;
  }
  memset(buf, 0, BUFSIZ);

  sz = recv(sockfd, buf, BUFSIZ, 0);
  if ((sz) < 0)
  {
    HANDLE_SOCKET_ERRNO_AND_RETURN_NULL;
  }
  else if (sz == 0)
  {
    printf(YELLOW "Connection closed\n" RESET);
    return NULL;
  }
  else
  {
    response *res = response_parse(buf);
    return res;
  }
}
