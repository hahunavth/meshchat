#include "utils/string.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char** argv)
{
	const char* str1 = "Hello ";	
	const char* str2 = "World ";
	
	char* _str1 = string_new(str1);
	char* _str2 = string_new_n(str2, 2);
	assert(strcmp(str1, _str1) == 0);
	assert(strlen(_str2) == 2);
	assert(strncmp(str2, _str2, 2) == 0);

	// char* str3  = string_append(str1, str2);
	// assert(strcmp(str3, "Hello World") == 0);

	string_clean();

	return 0;
}