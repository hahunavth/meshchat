#ifndef __CLIENT_H__
#define __CLIENT_H__

#include <stdint.h>

int connect_server(const char *str, uint16_t port);
void close_conn();

#endif
