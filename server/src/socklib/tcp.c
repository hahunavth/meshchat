#include "socklib/tcp.h"

#include <assert.h>

socket_t tcp4_create_listener(const char* addr, in_port_t port, int backlogs)
{
    socket_t sockfd = socklib_create_socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

    struct sockaddr_in servaddr;
    servaddr.sin_family = AF_INET;
    servaddr.sin_port   = htons(port);
    servaddr.sin_addr.s_addr = inet_addr(addr);

    int res = bind(sockfd, (const struct sockaddr*)&servaddr, sizeof(struct sockaddr_in));
	if(res == -1){
		LOG_ERR("bind");
		abort();
	}

    res = listen(sockfd, backlogs);
	if(res == -1){
		LOG_ERR("listen");
		abort();
	}
	
    return sockfd;
}

socket_t tcp4_accept(socket_t lsfd, struct sockaddr* client)
{
	socket_t cfd;
	for(;;){
		socklen_t socklen = sizeof(struct sockaddr_in);
		if(cfd = accept(lsfd, client, &socklen) < 0){
			if(errno != EINTR){
				LOG_ERR("accept");
				abort();
			}
		}else{
			break;
		}
	}
	socklib_register_sockfd(cfd);
	return cfd;
}

void tcp4_accept_do_job(socket_t lsfd, struct sockaddr* client, void (*job)(void*), void* arg)
{
    socklen_t socklen = sizeof(struct sockaddr_in);
    socket_t cfd = accept(lsfd, client, &socklen);
	if(cfd == -1){
		LOG_ERR("accept");
		abort();
	}
	socklib_register_sockfd(cfd);
    job(arg);
    shutdown(cfd, SHUT_RDWR);
    socklib_close_socket(cfd);
}

socket_t tcp4_create_connector(const char* addr, in_port_t port)
{
	socket_t sockfd = socklib_create_socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

	struct sockaddr_in servaddr;
    servaddr.sin_family = AF_INET;
    servaddr.sin_port   = htons(port);
    servaddr.sin_addr.s_addr = inet_addr(addr);

	if(connect(sockfd, (struct sockaddr*)&servaddr, sizeof(struct sockaddr_in)) == -1){
		LOG_ERR("connect");
		abort();
	}

	return sockfd;
}