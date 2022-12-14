/**
 * @file common.h
 * 
 * @brief common operations of socklib
 * 
 * @author user2410
*/

#ifndef __SOCKLIB_COMMON_H
#define __SOCKLIB_COMMON_H

#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>

typedef int socket_t;

#define SOCKLIB_INVALID_SOCK -1

#define SOCKLIB_LOG_ERR(func) {\
	pid_t pid = getpid();\
	pthread_t tid = pthread_self();\
	fprintf(stderr, "pid = %d, tid = %lu, file: %s, line: %d, %s() error, errno = %d: %s\n", pid, tid, __FILE__, __LINE__, func, errno, strerror(errno));\
}

/** 
 * Init socklib. Should be called before any operation of socklib
 */
void socklib_init();

/** 
 * Register a new socket descriptor 
 * 
 * @param domain communication domain (PF_INET, PF_INET6)
 * @param type communication semantics (SOCK_STREAM, SOCK_DGRAM)
 * @param protocol a  particular protocol to be used with the socket (IPPROTO_IP, IPPROTO_TCP, IPPROTO_UDP)
 * 
 * @return a new socket descriptor
 * */
socket_t socklib_create_socket(int domain, int type, int protocol);

/**
 * Register a socket descriptor
 * 
 * @param sockfd socket descriptor to be registered
*/
void socklib_register_sockfd(socket_t sockfd);

/**
 * Check if a socket is open
 * 
 * @param sockfd socket descriptor to be checked
 * 
 * @return 0 if the socket is not opened, 1 otherwise
 * */
int socklib_is_sock_open(socket_t sockfd);

/** For debugging purpose only */
void print_sock_list();

/**
 * Close a socket
 * 
 * @param sockfd socket descriptor to be closed
 */
void socklib_close_socket(socket_t sockfd);

/** Do cleanup job: close all opened socket */
void socklib_destroy();

#endif
