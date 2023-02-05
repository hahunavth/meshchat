#include "utils/random.h"

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define ALPHABET_LEN 62

const char* alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

int rand_int(int min, int max)
{
	srand(time(NULL));
	int interval = max-min;
	return (rand()%interval + min);
}

void rand_str(char* res, size_t n)
{
	for(size_t i=0; i<n; i++){
		res[i] = alphabet[rand_int(0, ALPHABET_LEN)];
	}
	res[n] = '\0';
}

void rand_email(char* res, size_t n)
{
	rand_str(res, n);
	sprintf(res+n, "@mail.com");
}