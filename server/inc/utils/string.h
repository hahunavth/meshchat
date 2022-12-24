/**
 * @file string.h
 * 
 * @brief manage all heap allocated string
 * 
 * @author user2410
*/
#ifndef __UTILS_STRING_H
#define __UTILS_STRING_H

#include <stddef.h>

char* string_new(const char* str);
char* string_new_n(const char* str, size_t n);
char* string_append(const char* str1, const char* str2);
void string_remove(char* str);
void string_clean();

#endif