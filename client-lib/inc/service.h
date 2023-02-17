#ifndef __CLIENT_LIB_INC_SERVICE_H__
#define __CLIENT_LIB_INC_SERVICE_H__

#include "common.h"
#include "auth.h"
#include <inttypes.h>

/**
 * utils
 */
#define CHECK_MSG(op, msg) \
    do                     \
    {                      \
        if (!(op))         \
        {                  \
            return 1;      \
        }                  \
    } while (0)
// printf("%s (%s:%d)\n", msg, __FILE__, __LINE__);

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

#define PRINT_STATUS_CODE(status_code) \
    {                                  \
    }

// setvbuf(stdout, NULL, _IONBF, 0);
// printf(YELLOW "status_code: %d\n" RESET, status_code);
// fflush(stdout);
// __fpurge(stdout);
// console_printf(YELLOW "status_code: %d\n" RESET, status_code);

/**
 * External API
 */
extern int __recv(char *buff);
extern void ___send(const char *buff);

extern int connect_server(const char *addr, uint16_t port);
extern int get_sockfd();
extern void close_conn();
extern response *api_call(const int sockfd, const char *req);

extern int get_auth(char *_token, uint32_t *_uid);
extern int is_authenticated();
extern uint32_t _get_uid();
extern char *_get_token();

extern int __login(const char *username, const char *password, response_auth *_res);
extern int __register(const request_auth *req, response_auth *_res);
extern int __logout(const char *token, const char *user_id);

extern int _login(const int sockfd, const char *username, const char *password);
extern int _register(const int sockfd, const request_auth *req);
extern int _logout(const int sockfd);

extern int _get_user_info(const int sockfd, const uint32_t user2_id, response_user *_res);
extern int _get_user_search(const int sockfd, const char *uname, const int32_t offset, int32_t limit, uint32_t *_idls, uint32_t *_len);
extern int _create_conv(const int sockfd, const char *gname, uint32_t *_gid);
extern int _drop_conv(const int sockfd, const uint32_t conv_id);
extern int _join_conv(const int sockfd, const uint32_t conv_id, const uint32_t user2_id);
extern int _quit_conv(const int sockfd, const uint32_t conv_id, const uint32_t user2_id);
extern int _get_conv_info(const int sockfd, const uint32_t conv_id, uint32_t *_admin_id, char *_gname);
extern int _get_conv_members(const int sockfd, const uint32_t conv_id, uint32_t *_res, uint32_t *_len);
extern int _get_conv_list(const int sockfd, const int limit, const int offset, uint32_t *_idls, uint32_t *_len);
extern int _create_chat(const int sockfd, const uint32_t user2_id, uint32_t *chat_id);
extern int _delete_chat(const int sockfd, const uint32_t chat_id);
extern int _get_chat_list(const int sockfd, const int limit, const int offset, uint32_t *_idls, uint32_t *_len);
extern int _get_chat_info(const int sockfd, const uint32_t chat_id, uint32_t *_mem1_id, uint32_t *_mem2_id);
extern int _get_msg_all(const int sockfd, const int limit, const int offset, const uint32_t conv_id, const uint32_t chat_id, uint32_t *_msg_idls, uint32_t *_len);
extern int _get_msg_detail(const int sockfd, const uint32_t msg_id, response_msg *_msg);
extern int _send_msg_text(const int sockfd, const uint32_t conv_id, const uint32_t chat_id, const uint32_t reply_to, const char *msg, uint32_t *_msg_id);
extern int _send_msg_file(const int sockfd, const uint32_t conv_id, const uint32_t chat_id, const uint32_t reply_to, const char *msg, uint32_t *_msg_id);
extern int _delete_msg(const int sockfd, const uint32_t msg_id);
extern int _notify_new_msg(const int sockfd, uint32_t *_idls, uint32_t *_len);
extern int _notify_del_msg(const int sockfd, const uint32_t conv_id, const uint32_t chat_id, uint32_t *_idls, uint32_t *_len);

extern int _get_msg_detail_raw(const int sockfd, const uint32_t msg_id,
                               uint32_t *_chat_id, uint32_t *_conv_id, uint32_t *_reply_to,
                               uint32_t *_from_uid, uint32_t *_created_at,
                               uint32_t *_content_type, uint32_t *_content_length,
                               char *_msg_content);

#endif
