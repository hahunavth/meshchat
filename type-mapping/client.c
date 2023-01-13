#include "client.h"
// #include "request.h"
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>

#define BACKLOG 100

static int sockfd = -1;
// create token array here

// int register(char *username, )
// {
//   char buf[BUFSIZ];
//   make_reques();
//   int rc = write(sockfd, buf, BUFSIZ);

//   read(sockfd, buf);
//   response_parse();
// }
// void init()
// {
//   printf("init!\n");
//   __sighandler_t ret = signal(SIGPIPE, SIG_IGN);

//   if (ret == SIG_ERR)
//   {
//     perror("signal");
//     exit(1);
//   }
// }

int connect_server(const char *addr, uint16_t port)
{
  sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
  if (sockfd < 0)
    return -1;

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

int simple_send(char *str)
{
  int c = send(sockfd, str, strlen(str), 0);
  // = 0 close
  if (c == 0)
  {
    puts("Server closed connection");
    close_conn();
  }
  // < 0 error
  if (c < 0)
  {
    perror("\nError: ");
    close_conn();
  }
}

char *simple_recv()
{
  if (sockfd == -1)
    return NULL;

  char *buff = (char *)calloc(1024, sizeof(char));
  int bytes_recv = recv(sockfd, buff, 1024, 0);
  if (bytes_recv == 0)
  {
    free(buff);
    close_conn();
  }

  if (bytes_recv < 0)
  {
    free(buff);
    perror("\nError: ");
    close_conn();
    exit(1);
  }

  buff[bytes_recv] = '\0';
  return buff;
}

char *send_and_recv(char *str)
{
  simple_send(str);
  return simple_recv();
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
