/**
 * @file udp.h
 * 
 * @brief socket operations using udp protocol
 * 
 * @author user2410
*/

#ifndef __SOCKLIB_UDP_H
#define __SOCKLIB_UDP_H

#include "common.h"

/**
 * Create a bound udp socket
 * 
 * @return bound socket descriptor
*/
socket_t udp4_create_bound_socket();

/**
 * Create a listening udp socket on ipv4 interface
 * 
 * @param addr ipv4 format string interface address (e.g: "127.0.0.1")
 * @param port 16-bit port number of port to be bound
 * 
 * @return listening socket descriptor
*/
socket_t udp4_create_listener(const char *addr, in_port_t port);

#endif