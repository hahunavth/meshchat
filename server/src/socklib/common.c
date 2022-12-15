#include "socklib/common.h"
#include "utils/sll.h"

#include <assert.h>
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>

static sllnode_t* head = NULL;
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
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
	sll_insert_node(&head, new_jval_i(sockfd));
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
	Jval val = new_jval_i(sockfd);
	if(!sll_find_node(head, val, NULL)){
		sll_insert_node(&head, val);
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
	int res = sll_find_node(head, new_jval_i(sockfd), NULL);
	pthread_mutex_unlock(&mutex);
	return res;
}

void print_sock_list()
{
	sllnode_t *node = head;
	printf("\nSocket list: ");
	while(node){
		printf("%d ", (node->val).i);
		node = node->next;
	}
	puts("");
}

static void free_node(Jval v)
{
	close(v.i);
}

void socklib_close_socket(socket_t sockfd)
{
	if(!is_socklib_init){
	    fprintf(stderr, "socklib not initialized\n");
	    return;
	};
    
	pthread_mutex_lock(&mutex);
	sll_delete_free_node(&head, new_jval_i(sockfd), free_node);
	pthread_mutex_unlock(&mutex);
}

void socklib_destroy()
{
	if(!is_socklib_init) return;
    
    puts("\nDestroying socklib ...\n");
	struct node_t *node;

	pthread_mutex_lock(&mutex);
	sll_remove_free(&head, free_node);
	pthread_mutex_unlock(&mutex);

	signal(SIGPIPE, SIG_DFL);

	is_socklib_init = 0;
}