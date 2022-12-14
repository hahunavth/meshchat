#include "socklib/udp.h"

#include <assert.h>

socket_t udp4_create_bound_socket()
{
	return socklib_create_socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
}

socket_t udp4_create_listener(const char *addr, in_port_t port)
{
	socket_t sockfd = udp4_create_bound_socket();
	if(sockfd == SOCKLIB_INVALID_SOCK)
		return SOCKLIB_INVALID_SOCK;

	struct sockaddr_in servaddr;
    servaddr.sin_family = AF_INET;
    servaddr.sin_port   = htons(port);
    servaddr.sin_addr.s_addr = inet_addr(addr);

	if(bind(sockfd, (const struct sockaddr*)&servaddr, sizeof(struct sockaddr_in)) == -1)
	{
		socklib_close_socket(sockfd);
		SOCKLIB_LOG_ERR("bind");
		return SOCKLIB_INVALID_SOCK;
	}

	return sockfd;
}