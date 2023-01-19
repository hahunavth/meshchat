#ifndef __HANDLER_H
#define __HANDLER_H

#include "request.h"

#include <netinet/in.h>

int init_handler(const char *db_file, const char *secrete_key, void (*close_sock_fn)(int));

void handle_req(int epoll_fd, int cfd);

void destroy_handler();

#endif