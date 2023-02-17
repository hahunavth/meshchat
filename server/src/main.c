#include "handler.h"
#include "utils/string.h"

#include <arpa/inet.h>
#include <fcntl.h>
#include <getopt.h>
#include <netinet/in.h>
#include <pthread.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <sys/epoll.h>
#include <sys/select.h>
#include <unistd.h>

/* Constants */
#define DEFAULT_SERVER_PORT 9000
#define SERVER_BACKLOG 32
#define DEFAULT_NTH_WORKERS 8
#define EVENTS_BUF_SIZE 256
#define SECRETE_KEY "Key of encryption"

/* Macros */
#define PRINT_USAGE() fprintf(stderr, "Usage: %s {domain: 4|6} [-i interface address] [-p port]\n", argv[0])

/* Global variables */
static int listen_fd;
static int nths;
static pthread_t *thpool;
static int epoll_fd;

static int maxfd;
static fd_set fdset;
static pthread_mutex_t fdset_lock = PTHREAD_MUTEX_INITIALIZER;

/* Function prototypes */
void close_sock(int);
static void shutdown_server(int signo);

static void fdset_add(int fd);
static void fdset_clr(int fd);

static void *th_func(void *arg);

/* Entry point */
int main(int argc, char **argv)
{
	int opt, rc, on = 1;
	struct in_addr serv_in_addr = {INADDR_ANY};
	char addr_str[INET6_ADDRSTRLEN];
	uint16_t serv_port = DEFAULT_SERVER_PORT;

	signal(SIGINT, shutdown_server);
	signal(SIGABRT, shutdown_server);
	signal(SIGSEGV, shutdown_server);

	if (!init_handler("./db/meshserver.db", SECRETE_KEY, close_sock))
	{
		puts("init_handler() failed");
		exit(EXIT_FAILURE);
	}

	/* Get options */
	if (argc > 1)
	{
		while ((opt = getopt(argc, argv, "i:p:")) != -1)
		{
			switch (opt)
			{
			case 'i':
				rc = inet_pton(AF_INET, optarg, &serv_in_addr);
				if (rc < 0)
					perror("inet_pton");
				else if (rc == 0)
					puts("not in presentation format");
				break;
			case 'p':
				sscanf(optarg, "%hu", &serv_port);
				if (serv_port == 0)
					serv_port = DEFAULT_SERVER_PORT;
				break;
			default:
				PRINT_USAGE();
				exit(EXIT_FAILURE);
			}
		}
	}

	/* Create a stream socket */
	listen_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (listen_fd < 0)
	{
		perror("socket() failed");
		exit(EXIT_FAILURE);
	}

	/* Allow socket descriptor to be reuseable */
	if (setsockopt(listen_fd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
	{
		perror("setsockopt() failed");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}

	/* Set socket to be nonblocking */
	rc = fcntl(listen_fd, F_GETFL, 0);
	if (rc < 0)
	{
		perror("fcntl() get sock opt fails failed");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}
	if (fcntl(rc, F_SETFL, rc | O_NONBLOCK) < 0)
	{
		perror("fcntl() set nonblocking fails failed");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}

	/* Bind the socket */
	struct sockaddr_in addr;
	memset(&addr, 0, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_addr = serv_in_addr;
	addr.sin_port = htons(serv_port);
	if (bind(listen_fd, (struct sockaddr *)&addr, sizeof(addr)) < 0)
	{
		perror("bind() failed");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}

	/* Set the listen back log */
	if (listen(listen_fd, SERVER_BACKLOG) < 0)
	{
		perror("listen() failed");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}

	/* Display listening address */
	if (inet_ntop(AF_INET, (void *)&serv_in_addr, addr_str, sizeof(struct sockaddr_in)))
		printf("Listening on %s:%hu\n", addr_str, serv_port);

	/* Initialize epoll instance */
	if ((epoll_fd = epoll_create(1)) < 0)
	{
		perror("epoll_create() failed");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}

	/* Initialize fdset */
	FD_ZERO(&fdset);
	FD_SET(listen_fd, &fdset);
	maxfd = listen_fd;

	/* Register event for listen_fd */
	struct epoll_event epevent;
	epevent.events = EPOLLIN | EPOLLET;
	epevent.data.fd = listen_fd;

	if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, listen_fd, &epevent) < 0)
	{
		perror("epoll_ctl() failed on main thread");
		close(listen_fd);
		exit(EXIT_FAILURE);
	}

	/* Query number of online processors */
	rc = sysconf(_SC_NPROCESSORS_ONLN);
	if (rc < 0)
		perror("sysconf() failed");
	else
	{
		printf("%d processors available\n", rc);
		nths = rc + 1;
	}

	/* Init threadpool */
	thpool = (pthread_t *)calloc(nths, sizeof(pthread_t));
	if (!thpool)
	{
		close(listen_fd);
		exit(EXIT_FAILURE);
	}
	for (int i = 0; i < nths; i++)
	{
		if (pthread_create(&(thpool[i]), NULL, th_func, NULL) < 0)
		{
			fprintf(stderr, "pthread_create failed: %s", strerror(rc));
			shutdown_server(EXIT_FAILURE);
		}
	}

	th_func(NULL);
}

/*******************************/

void close_sock(int fd)
{
	epoll_ctl(epoll_fd, EPOLL_CTL_DEL, fd, NULL);
	fcntl(fd, F_SETFL, fcntl(fd, F_GETFL, 0) | O_NONBLOCK);
	close(fd);
	fdset_clr(fd);
}

void shutdown_server(int signo)
{
	int i, rc;
	void *res;

	printf("Server is about to shutdown with code %d\n", signo);

	/* Clean up all of the sockets that are open */
	for (i = 0; i < FD_SETSIZE; i++)
	{
		if (FD_ISSET(i, &fdset))
			close(i);
	}
	close(epoll_fd);

	/* Cancel all running workers and clean up all of worker threads */
	for (i = 0; i < nths; ++i)
	{
		if (thpool[i] == 0)
			continue;
		pthread_cancel(thpool[i]);
		rc = pthread_join(thpool[i], &res);
		if (rc != 0)
			fprintf(stderr, "pthread_join failed: %s\n", strerror(rc));

		if (res == PTHREAD_CANCELED)
			fprintf(stderr, "thread #%ld was canceled\n", thpool[i]);
		else
			printf("thread #%ld wasn't canceled (shouldn't happen!)\n", thpool[i]);
	}
	free(thpool);

	destroy_handler();

	/* Destroy heap-allocated strings */
	string_clean();

	exit(signo);
}

/*******************************/

void fdset_add(int fd)
{
	pthread_mutex_lock(&fdset_lock);
	FD_SET(fd, &fdset);
	if (fd > maxfd)
		maxfd = fd;
	pthread_mutex_unlock(&fdset_lock);
}

void fdset_clr(int fd)
{
	pthread_mutex_lock(&fdset_lock);
	FD_CLR(fd, &fdset);
	if (fd == maxfd)
	{
		while (!FD_ISSET(maxfd, &fdset))
			maxfd--;
	}
	pthread_mutex_unlock(&fdset_lock);
}

/*******************************/

void accept_new_client()
{
	int csock;
	struct sockaddr_in addr;
	socklen_t socklen = sizeof(addr);
	if ((csock = accept(listen_fd, (struct sockaddr *)&addr, &socklen)) < 0)
	{
		perror("accept failed");
		return;
	}

	// if (set_nonblocking(csock) < 0)
	// {
	// 	perror("fcntl() failed");
	// 	close(csock);
	// 	return;
	// }

	char addr_str[INET_ADDRSTRLEN];
	if (inet_ntop(AF_INET, &(addr.sin_addr), addr_str, sizeof(addr)))
	{
		printf("New incoming connection from %s:%hu\n", addr_str, ntohs(addr.sin_port));
	}

	struct epoll_event epevent;
	epevent.events = EPOLLIN | EPOLLET | EPOLLONESHOT;
	epevent.data.fd = csock;

	if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, csock, &epevent) < 0)
	{
		perror("epoll_ctl(2) failed attempting to add new client");
		close(csock);
		return;
	}

	fdset_add(csock);

	return;
}

void *th_func(void *arg)
{
	(void)arg;
	struct epoll_event events[EVENTS_BUF_SIZE];

	int nevents;
	int fd;
	while ((nevents = epoll_wait(epoll_fd, events, EVENTS_BUF_SIZE, -1)) >= 0)
	{
		if (nevents == 0)
		{
			fprintf(stdout, "epoll_wait(2) timed out, #%lu do some cleaning job", pthread_self());
		}

		for (int i = 0; i < nevents; i++)
		{
			fd = events[i].data.fd;
			if (events[i].events & EPOLLIN)
			{
				if (fd == listen_fd)
					accept_new_client();
				else
					handle_req(epoll_fd, fd);
			}
		}
	}

	fprintf(stderr, "epoll_wait(2) on #%lu error\n", pthread_self());

	return NULL;
}

/****************/
