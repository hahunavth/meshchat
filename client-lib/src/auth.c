#include "auth.h"
#include "utils.h"

// auth
static uint32_t __uid;
static char __token[TOKEN_LEN];
static int is_auth = 0;

void __set_auth(const response_auth *auth)
{
  LOCK;
  __uid = auth->user_id;
  memcpy(__token, auth->token, TOKEN_LEN);
  is_auth = 1;
  UNLOCK;
}

void __clear_auth()
{
  LOCK;
  __uid = 0;
  memset(__token, 0, TOKEN_LEN);
  is_auth = 0;
  UNLOCK;
}

int get_auth(char *_token, uint32_t *_uid)
{
  if (is_auth)
  {
    memcpy(_token, __token, TOKEN_LEN);
    *_uid = __uid;
    return 1;
  }
  return 0;
}

int is_authenticated()
{
  return is_auth;
}

uint32_t _get_uid() { return __uid; }
char *_get_token() { return __token; }
