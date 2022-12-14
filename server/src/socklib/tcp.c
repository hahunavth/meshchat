#include "socklib/tcp.h"

#include <assert.h>

socket_t tcp4_create_listener(const char *addr, in_port_t port, int backlogs)
{
	socket_t sockfd = socklib_create_socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (sockfd == SOCKLIB_INVALID_SOCK)
	{
		return SOCKLIB_INVALID_SOCK;
	}

	struct sockaddr_in servaddr;
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(port);
	servaddr.sin_addr.s_addr = inet_addr(addr);

	if (bind(sockfd, (const struct sockaddr *)&servaddr, sizeof(struct sockaddr_in)) == -1)
	{
		socklib_close_socket(sockfd);
		SOCKLIB_LOG_ERR("bind");
		return SOCKLIB_INVALID_SOCK;
	}

	if (listen(sockfd, backlogs) == -1)
	{
		socklib_close_socket(sockfd);
		SOCKLIB_LOG_ERR("bind");
		return SOCKLIB_INVALID_SOCK;
	}

	return sockfd;
}

socket_t tcp4_accept(socket_t lsfd, struct sockaddr *client)
{
	socket_t cfd;
	for (;;)
	{
		socklen_t socklen = sizeof(struct sockaddr_in);
		if ((cfd = accept(lsfd, client, &socklen)) == SOCKLIB_INVALID_SOCK)
		{
			if (errno != EINTR)
			{
				SOCKLIB_LOG_ERR("accept");
				socklib_close_socket(lsfd);
				return SOCKLIB_INVALID_SOCK;
			}
			/* Restart interupted operation */
			continue;
		}
		break;
	}
	socklib_register_sockfd(cfd);
	return cfd;
}

void tcp4_accept_do_job(socket_t lsfd, struct sockaddr *client, void (*job)(void *), void *arg)
{
	socket_t cfd = tcp4_accept(lsfd, client);
	if (cfd == SOCKLIB_INVALID_SOCK)
		return;
	
	socklib_register_sockfd(cfd);
	job(arg);
	shutdown(cfd, SHUT_RDWR);
	socklib_close_socket(cfd);
}

socket_t tcp4_create_connector(const char *addr, in_port_t port)
{
	socket_t sockfd = socklib_create_socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (sockfd == SOCKLIB_INVALID_SOCK)
		return SOCKLIB_INVALID_SOCK;
	
	struct sockaddr_in servaddr;
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(port);
	servaddr.sin_addr.s_addr = inet_addr(addr);

	if (connect(sockfd, (struct sockaddr *)&servaddr, sizeof(struct sockaddr_in)) == -1)
	{
		socklib_close_socket(sockfd);
		SOCKLIB_LOG_ERR("connect");
		return SOCKLIB_INVALID_SOCK;
	}

	return sockfd;
}