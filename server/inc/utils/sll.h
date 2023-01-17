#ifndef __UTILS_SLL_H
#define __UTILS_SLL_H

#include "libfdr/jval.h"

#include <stddef.h>

typedef struct sllnode_t
{
	Jval val;
	struct sllnode_t *next;
} sllnode_t;

void sll_insert_node(sllnode_t **head, Jval val);
size_t sll_length(sllnode_t *head);
sllnode_t *sll_find_node(sllnode_t *head, Jval val, sllnode_t **prev);
void sll_delete_node(sllnode_t **head, Jval val);
void sll_delete_free_node(sllnode_t **head, Jval val, void (*func)(Jval v));
void sll_remove(sllnode_t **head);
void sll_remove_free(sllnode_t **head, void (*func)(Jval v));

#endif