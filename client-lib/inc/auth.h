#ifndef __CLIENT_LIB_INC_AUTH_H__
#define __CLIENT_LIB_INC_AUTH_H__

#include "common.h"

extern void __set_auth(const response_auth *auth);
extern void __clear_auth();

extern int get_auth(char *_token, uint32_t *_uid);
extern int is_authenticated();

extern uint32_t _get_uid();
extern char *_get_token();

extern void console_printf(const char *fmt, ...);

#endif
