#include "request.h"
#include "response.h"
#include "sql.h"
#include "utils/sll.h"
#include "utils/string.h"

#include <arpa/inet.h>
#include <assert.h>
#include <errno.h>
#include <fcntl.h>
#include <getopt.h>
#include <netinet/in.h>
#include <pthread.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/epoll.h>
#include <sys/select.h>
#include <time.h>
#include <unistd.h>

#include <openssl/blowfish.h>
#include <openssl/md5.h>

/* Constants */
#define DEFAULT_SERVER_PORT 9000
#define SERVER_BACKLOG 32
#define DEFAULT_NTH_WORKERS 8
#define EVENTS_BUF_SIZE 256
#define SECRETE_KEY "Key of encryption"
#define EXPIRY_TIME 108000
#define TOKEN_LEN 16
#define HASHED_LEN 16

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

sqlite3 *db = NULL;

static BF_KEY key;

/* Function prototypes */
static void shutdown_server(int signo);

static void fdset_add(int fd);
static void fdset_clr(int fd);

static int set_nonblocking(int fd);
static void *th_func(void *arg);
static void handle_req(int cfd);

static void make_token(in_addr_t addr, uint32_t user_id, char res[TOKEN_LEN]);
static int verify_token(uint32_t addr, uint32_t user_id, const char token[TOKEN_LEN]);
static int hash_str(const char *str, char *res);

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

	/* Connect to db */
	if ((rc = sqlite3_open("./db/meshserver.db", &db)) != SQLITE_OK)
	{
		fprintf(stderr, "Can't open database: %s\n", sqlite3_errmsg(db));
		exit(EXIT_FAILURE);
	}
	puts("Connected to database");

	/* Set up secret key */
	BF_set_key(&key, strlen(SECRETE_KEY), (const unsigned char *)SECRETE_KEY);

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
	if (set_nonblocking(listen_fd) < 0)
	{
		perror("fcntl() failed");
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
	if (epoll_ctl(epoll_fd, EPOLL_CTL_DEL, fd, NULL) < 0)
	{
		perror("epoll_ctl(2) failed");
		abort();
	}
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

	/* Close database connection */
	sqlite3_close(db);

	/* Destroy allocated strings */
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

int set_nonblocking(int fd)
{
	int flags = fcntl(fd, F_GETFL, 0);
	if (flags == -1)
		return -1;
	if (fcntl(fd, F_SETFL, flags | O_NONBLOCK) == -1)
		return -1;
	return 0;
}

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

	if (set_nonblocking(csock) < 0)
	{
		perror("fcntl() failed");
		close(csock);
		return;
	}

	char addr_str[INET_ADDRSTRLEN];
	if (inet_ntop(AF_INET, &(addr.sin_addr), addr_str, sizeof(addr)))
	{
		printf("New incoming connection from %s:%hu\n", addr_str, ntohs(addr.sin_port));
	}

	struct epoll_event epevent;
	epevent.events = EPOLLIN | EPOLLET;
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
	while ((nevents = epoll_wait(epoll_fd, events, EVENTS_BUF_SIZE, -1)) > 0)
	{
		for (int i = 0; i < nevents; i++)
		{
			fd = events[i].data.fd;
			if (events[i].events & EPOLLIN)
			{
				if (fd == listen_fd)
					accept_new_client();
				else
					handle_req(fd);
			}
		}
	}

	return NULL;
}

#define RESPONSE_ERR(status, group, action)                      \
	{                                                            \
		make_err_response((uint32_t)status, group, action, buf); \
		if (write(cfd, buf, BUFSIZ) < 0)                         \
		{                                                        \
			perror("write() failed");                            \
			close_sock(cfd);                                     \
		}                                                        \
		return;                                                  \
	}

#define RESPONSE_ERR_FREE(status, group, action, resource, freefn) \
	{                                                              \
		freefn(resource);                                          \
		RESPONSE_ERR(status, group, action);                       \
	}

/* Handler routines */
void handle_auth_register(int cfd, in_addr_t addr, request *req, char *buf);
void handle_auth_login(int cfd, in_addr_t addr, request *req, char *buf);
/****************/
void handle_user_get_info(int cfd, request *req, char *buf);
void handle_user_search(int cfd, request *req, char *buf);
/****************/
void handle_conv_create(int cfd, request *req, char *buf);
void handle_conv_drop(int cfd, request *req, char *buf);
void handle_conv_join(int cfd, request *req, char *buf);
void handle_conv_quit(int cfd, request *req, char *buf);
void handle_conv_get_info(int cfd, request *req, char *buf);
void handle_conv_get_members(int cfd, request *req, char *buf);
void handle_conv_get_list(int cfd, request *req, char *buf);
/****************/
void handle_chat_create(int cfd, request *req, char *buf);
void handle_chat_drop(int cfd, request *req, char *buf);
void handle_chat_get_list(int cfd, request *req, char *buf);
/****************/
void handle_msg_get_all(int cfd, request *req, char *buf);
void handle_msg_get_detail(int cfd, request *req, char *buf);
void handle_msg_send(int cfd, request *req, char *buf);
void handle_msg_delete(int cfd, request *req, char *buf);
/****************/
void handle_req(int cfd)
{
	int rc;
	char buf[BUFSIZ + 1];
	struct sockaddr_in addr;
	socklen_t socklen = sizeof(addr);
	ssize_t nbytes;

	printf("Thread #%lu working on %d\n", pthread_self(), cfd);

	if ((nbytes = recv(cfd, buf, BUFSIZ, 0)) < 0)
	{
		perror("recv() failed");
		close_sock(cfd);
		return;
	}

	if (nbytes == 0)
	{
		printf("Connection closed on %d\n", cfd);
		close_sock(cfd);
		return;
	}

	if (getpeername(cfd, (struct sockaddr *)&addr, &socklen) >= 0)
	{
		char addr_str[INET_ADDRSTRLEN];
		if (inet_ntop(AF_INET, &(addr.sin_addr), addr_str, sizeof(addr)))
			printf("*** [%p] [%s:%hu] -> server: %ld bytes\n", (void *)pthread_self(), addr_str, ntohs(addr.sin_port), nbytes);
	}

	request *req = request_parse(buf);
	if (!req->body)
		RESPONSE_ERR(400, (req->header).group, (req->header).action);

	if ((req->header).group == 0)
	{
		switch ((req->header).action)
		{
		case 0x00:
			handle_auth_register(cfd, addr.sin_addr.s_addr, req, buf);
			break;
		case 0x01:
			handle_auth_login(cfd, addr.sin_addr.s_addr, req, buf);
			break;
		}
	}
	else
	{
		rc = verify_token(addr.sin_addr.s_addr, (req->header).user_id, (req->header).token);
		if (rc <= 0)
			RESPONSE_ERR(403, (req->header).group, (req->header).action);

		switch ((req->header).group)
		{
		case 0x01:
			switch ((req->header).action)
			{
			case 0x00:
				make_response_user_logout(200, buf);
				if (write(cfd, buf, BUFSIZ) < 0)
				{
					perror("write() failed");
					close_sock(cfd);
					return;
				}
				close_sock(cfd);
				break;
			case 0x01:
				handle_user_get_info(cfd, req, buf);
				break;
			case 0x02:
				handle_user_search(cfd, req, buf);
				break;
			}
			break;
		case 0x02:
			switch ((req->header).action)
			{
			case 0x00:
				handle_conv_create(cfd, req, buf);
				break;
			case 0x01:
				handle_conv_drop(cfd, req, buf);
				break;
			case 0x02:
				handle_conv_join(cfd, req, buf);
				break;
			case 0x03:
				handle_conv_quit(cfd, req, buf);
				break;
			case 0x04:
				handle_conv_get_info(cfd, req, buf);
				break;
			case 0x05:
				handle_conv_get_members(cfd, req, buf);
				break;
			case 0x06:
				handle_conv_get_list(cfd, req, buf);
				break;
			}
			break;
		case 0x03:
			switch ((req->header).action)
			{
			case 0x00:
				handle_chat_create(cfd, req, buf);
				break;
			case 0x01:
				handle_chat_drop(cfd, req, buf);
				break;
			case 0x02:
				handle_chat_get_list(cfd, req, buf);
				break;
			}
			break;
		case 0x04:
			switch ((req->header).action)
			{
			case 0x00:
				handle_msg_get_all(cfd, req, buf);
				break;
			case 0x01:
				handle_msg_get_detail(cfd, req, buf);
				break;
			case 0x02:
				handle_msg_send(cfd, req, buf);
				break;
			case 0x03:
				handle_msg_delete(cfd, req, buf);
				break;
			case 0x04:
				// handle_msg_get_all(cfd, req, buf);
				break;
			case 0x05:
				// handle_msg_get_all(cfd, req, buf);
				break;
			}
			break;
		}
	}
	request_destroy(req);
}

/*******************************/

void handle_auth_register(int cfd, in_addr_t addr, request *req, char *buf)
{
	int rc;
	char token[TOKEN_LEN];
	char hashed_password[(HASHED_LEN << 1) + 1];

	request_auth *ra = &((req->body)->r_auth);

	if (hash_str(ra->password, hashed_password) < 0)
		RESPONSE_ERR(500, 0, 0);

	user_schema user = {
		.uname = ra->uname,
		.password = hashed_password, /* Hash password before saving */
		.email = ra->email,
		.phone = ra->phone};

	/* Save the new user to db */
	uint32_t id = user_create(db, &user, &rc);
	if (sql_is_err(rc))
	{
		if(rc == SQLITE_CONSTRAINT)
			RESPONSE_ERR(409, 0, 0); /* An user with such uname has already existed*/
		RESPONSE_ERR(500, 0, 0);
	}

	make_token(addr, id, token);
	make_response_auth_register(201, token, id, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
		return;
	}
}

void handle_auth_login(int cfd, in_addr_t addr, request *req, char *buf)
{
	int rc;
	char token[TOKEN_LEN];
	char hashed_password[(HASHED_LEN << 1) + 1];

	request_auth *ra = &((req->body)->r_auth);

	user_schema *user = user_get_by_uname(db, ra->uname, &rc);

	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 0, 1, user, user_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 0, 1, user, user_free);

	hash_str(ra->password, hashed_password);
	if (strcmp(hashed_password, user->password) != 0)
		RESPONSE_ERR_FREE(403, 0, 1, user, user_free);

	make_token(addr, user->id, token);
	make_response_auth_login(200, token, user->id, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	user_free(user);
}

/****************/

void handle_user_get_info(int cfd, request *req, char *buf)
{
	int rc;
	request_user *ru = &(req->body->r_user);
	user_schema *user = user_get_by_id(db, ru->user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 1, 1, user, user_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 1, 1, user, user_free);

	make_response_user_get_info(200, user->uname, user->phone, user->email, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	user_free(user);
}

void handle_user_search(int cfd, request *req, char *buf)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	request_user *ru = &(req->body->r_user);
	sllnode_t *ls = user_search_by_uname(db, ru->uname, limit, offset, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 1, 2, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_user_search(200, len, idls, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	sll_remove(&ls);
}

/****************/

void handle_conv_create(int cfd, request *req, char *buf)
{
	int rc;
	uint32_t id = conv_create(db, (req->header).user_id, (req->body->r_conv).gname, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 0);

	make_response_conv_create(201, id, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_conv_drop(int cfd, request *req, char *buf)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_member = conv_is_admin(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_member)
		RESPONSE_ERR(403, 2, 4);

	conv_drop(db, rconv->conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 1);

	make_response_conv_drop(200, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_conv_join(int cfd, request *req, char *buf)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_member = conv_is_admin(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_member)
		RESPONSE_ERR(403, 2, 4);

	conv_join(db, rconv->user_id, rconv->conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 2);

	make_response_conv_join(200, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_conv_quit(int cfd, request *req, char *buf)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_member = conv_is_member(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_member)
		RESPONSE_ERR(403, 2, 4);

	conv_quit(db, (req->header).user_id, (req->body->r_conv).conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 3);

	make_response_conv_quit(200, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_conv_get_info(int cfd, request *req, char *buf)
{
	int rc;
	conv_schema *conv = conv_get_info(db, (req->body->r_conv).conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 2, 4, conv, conv_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 2, 4, conv, conv_free);

	make_response_conv_get_info(200, conv->admin_id, conv->name, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_conv_get_members(int cfd, request *req, char *buf)
{
	int rc;
	request_conv *rconv = &(req->body->r_conv);
	int is_member = conv_is_member(db, rconv->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 2, 4);
	if (!is_member)
		RESPONSE_ERR(403, 2, 4);

	sllnode_t *ls = conv_get_members(db, rconv->conv_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 2, 4, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_conv_get_members(200, len, idls, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	sll_remove(&ls);
}

void handle_conv_get_list(int cfd, request *req, char *buf)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	sllnode_t *ls = user_get_conv_list(db, (req->header).user_id, limit, offset, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 2, 6, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_conv_get_list(200, len, idls, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	sll_remove(&ls);
}

/****************/

void handle_chat_create(int cfd, request *req, char *buf)
{
	int rc;
	uint32_t id = chat_create(db, (req->header).user_id, (req->body->r_chat).user_id2, &rc);
	if (sql_is_err(rc))
	{
		if(rc == SQLITE_CONSTRAINT)
			RESPONSE_ERR(409, 3, 0);	/* The chat has already exist */
		RESPONSE_ERR(500, 3, 0);
	}

	make_response_chat_create(201, id, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_chat_drop(int cfd, request *req, char *buf)
{
	int rc;
	request_chat *rchat = &(req->body->r_chat);
	int is_member = chat_is_member(db, rchat->chat_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 3, 1);
	if (!is_member)
		RESPONSE_ERR(403, 3, 1);

	chat_drop(db, rchat->chat_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 3, 1);

	make_response_chat_delete(200, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
}

void handle_chat_get_list(int cfd, request *req, char *buf)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	sllnode_t *ls = user_get_chat_list(db, (req->header).user_id, limit, offset, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 3, 2, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_chat_get_list(200, len, idls, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	sll_remove(&ls);
}

/****************/

void handle_msg_get_all(int cfd, request *req, char *buf)
{
	int rc;
	int limit = (req->header).limit > 0 ? (req->header).limit : 0;
	int offset = (req->header).offset > 0 ? (req->header).offset : 0;
	uint32_t chat_id = (req->body->r_msg).chat_id;
	uint32_t conv_id = (req->body->r_msg).conv_id;

	int is_member = conv_is_member(db, conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 4, 0);

	if (!is_member)
	{
		if (sql_is_err(rc))
			RESPONSE_ERR(500, 4, 0);
		if (!is_member)
			RESPONSE_ERR(403, 4, 0);
	}

	sllnode_t *ls;
	if (chat_id > 0)
		ls = msg_chat_get_all(db, chat_id, limit, offset, &rc);
	else
		ls = msg_conv_get_all(db, conv_id, limit, offset, &rc);

	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 0, &ls, sll_remove);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 0, &ls, sll_remove);

	size_t len = sll_length(ls);
	uint32_t idls[len];
	sllnode_t *iter = ls;
	for (size_t i = 0; i < len; i++)
	{
		idls[i] = (iter->val).l;
		iter = iter->next;
	}

	make_response_msg_get_all(200, len, idls, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	sll_remove(&ls);
}

void handle_msg_get_detail(int cfd, request *req, char *buf)
{
	int rc;

	msg_schema *msg = msg_get_detail(db, (req->body->r_msg).msg_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 1, msg, msg_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 1, msg, msg_free);

	int is_member = conv_is_member(db, msg->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 1, msg, msg_free);

	if (!is_member)
	{
		if (sql_is_err(rc))
			RESPONSE_ERR_FREE(500, 4, 1, msg, msg_free);
		if (!is_member)
			RESPONSE_ERR_FREE(404, 4, 1, msg, msg_free);
	}

	response_msg rm = {
		.msg_id = msg->id, .from_uid = msg->from_uid, .reply_to = msg->reply_to, .conv_id = msg->conv_id, .chat_id = msg->chat_id, .created_at = msg->created_at, .msg_type = msg->type, .content_type = msg->content_type, .msg_content = msg->content};
	make_response_msg_get_detail(200, &rm, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
	msg_free(msg);
}

void handle_msg_send(int cfd, request *req, char *buf)
{
	int rc;
	request_msg *rm = &(req->body->r_msg);

	int is_member = conv_is_member(db, rm->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR(500, 4, 3);

	if (!is_member)
	{
		is_member = chat_is_member(db, rm->chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR(500, 4, 3);
		if (!is_member)
			RESPONSE_ERR(403, 4, 3);
	}

	msg_schema msg = {
		.from_uid = (req->header).user_id, .reply_to = rm->reply_id, .conv_id = rm->conv_id, .chat_id = rm->chat_id, .content_length = (req->header).content_len, .content_type = (req->header).content_type, .content = rm->msg_content};
	uint32_t id = msg_send(db, &msg, &rc);
	if (sql_is_err(rc))
	{
		if(rc == SQLITE_CONSTRAINT)
			RESPONSE_ERR(409, 4, 2);	/* conflicted from_uid, reply_to, chat_id, conv_id */
		RESPONSE_ERR(500, 4, 2);
	}

	make_responses_msg_send(201, id, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}

	/* TODO: implement sending file ft */
}

void handle_msg_delete(int cfd, request *req, char *buf)
{
	int rc;
	msg_schema *msg = msg_get_detail(db, (req->body->r_msg).msg_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);

	if (rc == SQLITE_EMPTY)
		RESPONSE_ERR_FREE(404, 4, 3, msg, msg_free);

	int is_member = conv_is_member(db, msg->conv_id, (req->header).user_id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);

	if (!is_member)
	{
		is_member = chat_is_member(db, msg->chat_id, (req->header).user_id, &rc);
		if (sql_is_err(rc))
			RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);
		if (!is_member)
			RESPONSE_ERR_FREE(403, 4, 3, msg, msg_free);
	}

	msg_delete(db, msg->id, &rc);
	if (sql_is_err(rc))
		RESPONSE_ERR_FREE(500, 4, 3, msg, msg_free);

	make_response_msg_delete(200, buf);
	if (write(cfd, buf, BUFSIZ) < 0)
	{
		perror("write() failed");
		close_sock(cfd);
	}
	msg_free(msg);
}

/*******************************/

void make_token(in_addr_t addr, uint32_t user_id, char res[TOKEN_LEN])
{
	char msg[16];
	memcpy(msg, &addr, 4);
	memcpy(msg + 4, &user_id, 4);
	time_t expiry = time(NULL) + EXPIRY_TIME;
	memcpy(msg + 8, &expiry, sizeof(time_t));

	BF_ecb_encrypt((const unsigned char *)msg, (unsigned char *)res, &key, BF_ENCRYPT);
	BF_ecb_encrypt((const unsigned char *)(msg + 8), (unsigned char *)(res + 8), &key, BF_ENCRYPT);
}

int verify_token(uint32_t addr, uint32_t user_id, const char token[TOKEN_LEN])
{
	char decrypt[16];
	time_t expiry;

	BF_ecb_encrypt((const unsigned char *)token, (unsigned char *)decrypt, &key, BF_DECRYPT);
	BF_ecb_encrypt((const unsigned char *)(token + 8), (unsigned char *)(decrypt + 8), &key, BF_DECRYPT);

	if (memcmp(decrypt, &addr, 4) != 0)
		return -1;
	if (memcmp(decrypt + 4, &user_id, 4) != 0)
		return -1;
	memcpy(&expiry, decrypt + 8, sizeof(time_t));
	// printf("addr = %u, user_id = %u, expiry = %lu\n", addr, user_id, expiry);
	if (expiry - time(NULL) > EXPIRY_TIME)
		return 0;
	return 1;
}

int hash_str(const char *str, char *res)
{
	MD5_CTX md5_ctx;
	char hashed[HASHED_LEN];

	if (MD5_Init(&md5_ctx) == 0)
		return -1;

	if (MD5_Update(&md5_ctx, str, strlen(str)) == 0)
		return -1;

	if (MD5_Final((unsigned char *)hashed, &md5_ctx) == 0)
		return -1;

	/* output a hexa string */
	for (int i = 0; i < HASHED_LEN; i++)
	{
		uint8_t b = hashed[i];
		sprintf(res + (i << 1), "%02x", b);
	}
	res[(HASHED_LEN << 1)] = '\0';

	return 0;
}