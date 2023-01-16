#include "utils/string.h"
#include "utils/sll.h"

#include <assert.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>

static sllnode_t *head = NULL;
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

char *string_new(const char *str)
{
	size_t str_len = strlen(str);
	char *newstr = (char *)malloc(str_len+1);
	strncpy(newstr, str, str_len+1);

	pthread_mutex_lock(&mutex);
	sll_insert_node(&head, new_jval_s(newstr));
	pthread_mutex_unlock(&mutex);
	return newstr;
}

char *string_new_n(const char *str, size_t n)
{
	char *newstr = (char *)malloc(n+1);
	strncpy(newstr, str, n);
	newstr[n] = '\0';

	pthread_mutex_lock(&mutex);
	sll_insert_node(&head, new_jval_s(newstr));
	pthread_mutex_unlock(&mutex);
	return newstr;
}

char *string_mem_n(const char *str, size_t n)
{
	char *newstr = (char *)malloc(n);
	memcpy(newstr, str, n);

	pthread_mutex_lock(&mutex);
	sll_insert_node(&head, new_jval_s(newstr));
	pthread_mutex_unlock(&mutex);
	return newstr;
}

char *string_append(const char *str1, const char *str2)
{
	size_t str1_len = strlen(str1);
	size_t str2_len = strlen(str2);
	size_t new_len = str1_len + str2_len;
	char* newstr  = (char *)malloc(new_len + 1);
	assert(newstr);
	strncpy(newstr, str1, str1_len);
	strncpy(newstr + str1_len, str2, str2_len+1);

	pthread_mutex_lock(&mutex);
	printf("Insert new string(%p): %s\n", newstr, newstr);
	sll_insert_node(&head, new_jval_s(newstr));
	pthread_mutex_unlock(&mutex);
	
	return newstr;
}

static void free_node(Jval v)
{
	free(v.s);
}

void string_remove(char *str)
{
	sllnode_t *node;
	sllnode_t *prev;
	pthread_mutex_lock(&mutex);
	sll_delete_free_node(&head, new_jval_s(str), free_node);
	pthread_mutex_unlock(&mutex);
}

void string_clean()
{
	sllnode_t *node;
	pthread_mutex_lock(&mutex);
	sll_remove_free(&head, free_node);
	pthread_mutex_unlock(&mutex);
	head = NULL;
}