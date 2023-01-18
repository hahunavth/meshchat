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
#include <assert.h>
#include "service.h"

void print_req_auth(request_auth *req)
{
  puts("print_req_auth:");
  printf(BLUE "\tuname: %s\n" RESET, req->uname);
  printf(BLUE "\temail: %s\n" RESET, req->email);
  printf(BLUE "\tpassword: %s\n" RESET, req->password);
  printf(BLUE "\tphone: %s\n" RESET, req->phone);
}
