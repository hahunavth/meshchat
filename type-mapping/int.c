#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "int.h"

// 2^e + r
#define calc(e, r)                  \
    {                               \
        long long s = 1;            \
        for (int i = 0; i < e; i++) \
            s *= 2;                 \
        s = s + r;                  \
        printf("> %lld\n", s);      \
        return s;                   \
    }

int8_t calc_int8_t(int e, int r)
    calc(e, r);

int16_t calc_int16_t(int e, int r)
    calc(e, r);

int32_t calc_int32_t(int e, int r)
    calc(e, r);

int64_t calc_int64_t(int e, int r)
    calc(e, r);

u_int8_t calc_u_int8_t(int e, int r)
    calc(e, r);

u_int16_t calc_u_int16_t(int e, int r)
    calc(e, r);

u_int32_t calc_u_int32_t(int e, int r)
    calc(e, r);

u_int64_t calc_u_int64_t(int e, int r)
    calc(e, r);

// ------------------------------------

#define tm(i)              \
    {                      \
        printf("> %d", i); \
        return i;          \
    }

int8_t tm_int8_t(int8_t e)
{
    char str[100];
    sprintf(str, ">>>> %d", e);
    puts(str);
    return e;
}
int16_t tm_int16_t(int16_t e)
{
    printf("> %d", e);
    fflush(stdout);
    return e;
}
int32_t tm_int32_t(int32_t e)
{
    printf("> %d", e);
    fflush(stdout);
    return e;
}
int64_t tm_int64_t(int64_t e)
{
    printf("> %ld", e);
    fflush(stdout);
    return e;
}
u_int8_t tm_u_int8_t(u_int8_t e)
{
    printf("> %d", e);
    setvbuf(stdout, NULL, _IONBF, 0);
    return e;
}
u_int16_t tm_u_int16_t(u_int16_t e)
{
    printf("> %d", e);
    fflush(stdout);
    return e;
}
u_int32_t tm_u_int32_t(u_int32_t e)
{
    printf("> %d", e);
    fflush(stdout);
    return e;
}
u_int64_t tm_u_int64_t(u_int64_t e)
{
    printf("> %ld", e);
    fflush(stdout);
    return e;
}