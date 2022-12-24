#ifndef __UTIL_RANDOM_H
#define __UTIL_RANDOM_H

#include <stddef.h>

int rand_int(int min, int max);
void rand_str(char* res, size_t n);

void rand_email(char* res, size_t n);

#endif