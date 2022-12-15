#include "utils/sll.h"

#include <assert.h>
#include <stddef.h>

int main(int argc, char** argv)
{
	sllnode_t* head = NULL;

	/* Insertion test */
	sll_insert_node(&head, new_jval_i(1));
	sll_insert_node(&head, new_jval_i(2));
	sll_insert_node(&head, new_jval_i(3));

	sllnode_t* node = head;
	assert((node->val).i == 3);
	node = node->next;
	assert((node->val).i == 2);
	node = node->next;
	assert((node->val).i == 1);
	node = node->next;
	assert(!node);

	/* Searching test */
	sllnode_t* prev = NULL;
	assert(sll_find_node(head, new_jval_i(2), &prev));
	assert((prev->val).i == 3);
	assert(!sll_find_node(head, new_jval_i(4), NULL));

	/* Deletion test */
	sll_delete_node(&head, new_jval_i(2));
	node = head;
	assert((node->val).i == 3);
	node = node->next;
	assert((node->val).i == 1);

	/* Complete removal*/
	sll_remove(&head);
	assert(!head);

	return 0;
}