#ifndef __TM_INT_H__
#define __TM_INT_H__

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

extern int8_t calc_int8_t(int e, int r);
extern int16_t calc_int16_t(int e, int r);
extern int32_t calc_int32_t(int e, int r);
extern int64_t calc_int64_t(int e, int r);
extern u_int8_t calc_u_int8_t(int e, int r);
extern u_int16_t calc_u_int16_t(int e, int r);
extern u_int32_t calc_u_int32_t(int e, int r);
extern u_int64_t calc_u_int64_t(int e, int r);

extern int8_t tm_int8_t(int8_t e);
extern int16_t tm_int16_t(int16_t e);
extern int32_t tm_int32_t(int32_t e);
extern int64_t tm_int64_t(int64_t e);
extern u_int8_t tm_u_int8_t(u_int8_t e);
extern u_int16_t tm_u_int16_t(u_int16_t e);
extern u_int32_t tm_u_int32_t(u_int32_t e);
extern u_int64_t tm_u_int64_t(u_int64_t e);

#endif