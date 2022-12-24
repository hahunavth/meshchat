#include "utils/sll.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void sll_insert_node(sllnode_t** head, Jval val)
{
	struct sllnode_t *node = (struct sllnode_t *)malloc(sizeof(struct sllnode_t));
	assert(node);
	node->val = val;
	node->next = *head;
	*head = node;
}

sllnode_t *sll_find_node(sllnode_t* head, Jval val, sllnode_t **prev)
{
	struct sllnode_t *node = head;
	struct sllnode_t *_prev = NULL;

	while (node)
	{
		if (memcmp(&(node->val), &val, sizeof(Jval)) == 0)
		{
			if (prev) *prev = _prev;
			return node;
		}
		_prev = node;
		node = node->next;
	}

	return NULL;
}

void sll_delete_node(sllnode_t** head, Jval val)
{
	struct sllnode_t *node;
	struct sllnode_t *prev;
	if ((node = sll_find_node(*head, val, &prev)))
	{
		if(prev) prev->next = node->next;
		if(node == *head)
		    *head = (*head)->next;
		free(node);
	}
}

void sll_delete_free_node(sllnode_t** head, Jval val, void (*func)(Jval v))
{
	struct sllnode_t *node;
	struct sllnode_t *prev;
	if ((node = sll_find_node(*head, val, &prev)))
	{
		if(prev) prev->next = node->next;
		if(node == *head)
		    *head = (*head)->next;
		func(val);
		free(node);
	}
}

void sll_remove(sllnode_t** head)
{
	sllnode_t* node;
	while(*head)
	{
		node = *head;
		*head = node->next;
		free(node);
	}
	*head = NULL;
}

void sll_remove_free(sllnode_t** head, void (*func)(Jval v))
{
	sllnode_t* node;
	while(*head)
	{
		node = *head;
		*head = node->next;
		func(node->val);
		free(node);
	}
	*head = NULL;
}