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

extern int get_auth(char *_token, uint32_t *_uid);
extern int is_authenticated();
extern uint32_t _get_uid();
extern char *_get_token();

extern int __login(const char *username, const char *password, response_auth *_res);
extern int __register(const request_auth *req, response_auth *_res);
extern int __logout(const char *token, const char *user_id);

extern int _login(const char *username, const char *password);
extern int _register(const request_auth *req);
extern int _logout();

extern int _get_user_info(const uint32_t user2_id, response_user *_res);
extern int _get_user_search(const char *uname, const int32_t offset, int32_t limit, uint32_t *_idls, uint32_t *_len);
extern int _create_conv(const char *gname, uint32_t *_gid);
extern int _drop_conv(const uint32_t conv_id);
extern int _join_conv(const uint32_t conv_id, const uint32_t user2_id);
extern int _quit_conv(const uint32_t conv_id);
extern int _get_conv_info(const uint32_t conv_id, uint32_t *_admin_id, char *_gname);
extern int _get_conv_members(const uint32_t conv_id, uint32_t *_res, uint32_t *_len);
extern int _get_conv_list(const int limit, const int offset, uint32_t *_res);
extern int _create_chat(const uint32_t user2_id, uint32_t *chat_id);
extern int _delete_chat(const uint32_t chat_id);
extern int _get_chat_list(const int limit, const int offset, uint32_t *_idls, uint32_t *_len);
extern int _get_msg_all(const int limit, const int offset, const uint32_t conv_id, uint32_t chat_id, uint32_t *_msg_idls);
extern int _get_msg_detail(const uint32_t msg_id, response_msg *_msg);
extern int _send_msg_text(const uint32_t user_id, const uint32_t conv_id, const uint32_t chat_id, const uint32_t reply_to, const char *msg, uint32_t *_msg_id);
extern int _delete_msg(const uint32_t msg_id);
extern int _notify_new_msg(const uint32_t user_id, uint32_t *_idls);
extern int _notify_del_msg(uint32_t *_idls);

#endif
