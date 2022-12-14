#include "socklib/common.h"

#include <assert.h>
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>

/////////////////////////////////////////

struct node_t
{
	socket_t sockfd;
	struct node_t *next;
};

static struct node_t *head = NULL;
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

static void insert_node(socket_t sockfd)
{
	struct node_t *node = (struct node_t *)malloc(sizeof(struct node_t));
	assert(node);
	node->sockfd = sockfd;
	node->next = head;
	head = node;
}

static struct node_t *find_node(socket_t sockfd, struct node_t **prev)
{
	struct node_t *node = head;
	struct node_t *_prev = NULL;

	while (node)
	{
		if (node->sockfd == sockfd)
		{
			if (prev) *prev = _prev;
			return node;
		}
		_prev = node;
		node = node->next;
	}

	return NULL;
}

static void delete_node(socket_t sockfd)
{
	struct node_t *node;
	struct node_t *prev;
	if ((node = find_node(sockfd, &prev)))
	{
		if(prev) prev->next = node->next;
		if(node == head)
		    head = head->next;
		free(node);
	}
}

void print_sock_list()
{
	struct node_t *node = head;
	printf("\nSocket list: ");
	while(node){
		printf("%d, ", node->sockfd);
		node = node->next;
	}
	puts("");
}

/////////////////////////////////////////

static int is_socklib_init = 0;

void socklib_init()
{
	if(is_socklib_init) return;
	puts("\nInitializing socklib ...\n");
	signal(SIGPIPE, SIG_IGN);
	is_socklib_init = 1;
}

socket_t socklib_create_socket(int domain, int type, int protocol)
{
    if(!is_socklib_init){
	    fprintf(stderr, "socklib not initialized\n");
	    return SOCKLIB_INVALID_SOCK;
	}
    
	socket_t sockfd = socket(domain, type, protocol);
	if(sockfd == SOCKLIB_INVALID_SOCK){
		SOCKLIB_LOG_ERR("socket");
		return SOCKLIB_INVALID_SOCK;
	}
	pthread_mutex_lock(&mutex);
	insert_node(sockfd);
	pthread_mutex_unlock(&mutex);
	
	return sockfd;
}

void socklib_register_sockfd(socket_t sockfd)
{
	if(!is_socklib_init){
	    fprintf(stderr, "socklib not initialized\n");
	    return;
	}
    
    pthread_mutex_lock(&mutex);
	if(!find_node(sockfd, NULL)){
		insert_node(sockfd);
	}
	pthread_mutex_unlock(&mutex);
}

int socklib_is_sock_open(socket_t sockfd)
{
	if(!is_socklib_init){
	    fprintf(stderr, "socklib not initialized\n");
	    return -1;
	}
    
    pthread_mutex_lock(&mutex);
	struct node_t *node = find_node(sockfd, NULL);
	pthread_mutex_unlock(&mutex);
	return (node != NULL);
}

void socklib_close_socket(socket_t sockfd)
{
	if(!is_socklib_init){
	    fprintf(stderr, "socklib not initialized\n");
	    return;
	};
    
    close(sockfd);
	pthread_mutex_lock(&mutex);
	delete_node(sockfd);
	pthread_mutex_unlock(&mutex);
}

void socklib_destroy()
{
	if(!is_socklib_init) return;
    
    puts("\nDestroying socklib ...\n");
	struct node_t *node;

	pthread_mutex_lock(&mutex);
	while (head)
	{
		close(head->sockfd);
		node = head;
		head = head->next;
		free(node);
	}
	pthread_mutex_unlock(&mutex);

	signal(SIGPIPE, SIG_DFL);

	is_socklib_init = 0;
}