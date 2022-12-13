/**
 * @file tcp.h
 * 
 * @brief socket operations using tcp protocol
 * 
 * @author user2410
*/

#ifndef __SOCKLIB_TCP_H
#define __SOCKLIB_TCP_H

#include "common.h"

/**
 * Create a listener bound to ipv4 interface for tcp server 
 * 
 * @param addr ipv4 format string interface address (e.g: "127.0.0.1")
 * @param port 16-bit port number of port to be bound
 * @param backlogs the maximum length to which the queue of pending connections may grow
 * 
 * @return listening socket descriptor
 * */
socket_t tcp4_create_listener(const char* addr, in_port_t port, int backlogs);

/**
 * Accept connections to socket bound to lsfd, return comm socket for client
 * 
 * @param lsfd socket descriptor of the server
 * @param client stores address of the client, null value would ignore this
 * 
 * @return file descriptor for the accepted socket
 * */
socket_t tcp4_accept(socket_t lsfd, struct sockaddr* client);

/**
 * Accept a connection, do job and destroy connection 
 * 
 * @param lsfd socket descriptor of the server
 * @param client store address of the client
 * @param job function to be executed
 * @param arg arguments of the function to be executed
 * 
 * @return file descriptor for the accepted socket
 * */
void tcp4_accept_do_job(socket_t lsfd, struct sockaddr* client, void (*job)(void*), void* arg);

/**
 * Create a connector connected to a remote address
 * 
 * @param addr ipv4 format string address (e.g: "127.0.0.1")
 * @param port 16-bit port number of port to be bound
 * 
 * @return connected socket desscriptor
*/
socket_t tcp4_create_connector(const char* addr, in_port_t port);

#endif
