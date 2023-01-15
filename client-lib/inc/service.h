#ifndef __CLIENT_LIB_INC_SERVICE_H__
#define __CLIENT_LIB_INC_SERVICE_H__

#include <inttypes.h>

#define TOKEN_LEN 16

/**
 * utils
 */
#define CHECK_MSG(op, msg)                                   \
    do                                                       \
    {                                                        \
        if (!(op))                                           \
        {                                                    \
            printf("%s (%s:%d)\n", msg, __FILE__, __LINE__); \
            return 1;                                        \
        }                                                    \
    } while (0)

#define CHECK_ECHO(op) CHECK_MSG(op, #op)

#define DEBUG_MSG(msg) printf("%s (%s:%d)\n", msg, __FILE__, __LINE__);

#define PRINT_USER_ID(user_id) printf("user_id: %" PRIu32 "\n", user_id);
#define PRINT_TOKEN(token)              \
    printf("token: ");                  \
    for (int i = 0; i < TOKEN_LEN; i++) \
    {                                   \
        printf("%02hhX ", token[i]);    \
    }                                   \
    puts("");
#define PRINT_STATUS_CODE(status_code) printf("status_code: %d\n", status_code);

/**
 * External API
 */
extern int connect_server(const char *addr, uint16_t port);
extern int get_sockfd();
extern void close_conn();

extern int __login(
    const char *username, const char *password,
    response_auth *_res);
extern int _login(const char *username, const char *password);

extern int __register(
    const request_auth *req,
    response_auth *_res);
extern int _register(const request_auth *req);

extern int _logout(const char *token, const char *user_id);

#endif
