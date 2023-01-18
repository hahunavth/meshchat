package com.meshchat.client.cnative;

import com.meshchat.client.cnative.req.RequestAuth;
import com.meshchat.client.cnative.res.ResponseAuth;
import com.meshchat.client.cnative.res.ResponseUser;
import jnr.ffi.LibraryLoader;
import jnr.ffi.LibraryOption;
import jnr.ffi.NativeLong;
import jnr.ffi.Struct;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.NativeLongByReference;
import jnr.ffi.types.int32_t;
import jnr.ffi.types.u_int16_t;
import jnr.ffi.types.u_int32_t;

/**
 * Map function:
 * const -> @In
 * const char * -> @In CharSequence
 * char * -> @Out CharSequence
 * uint32_t -> @u_int32_t long
 * uint16_t -> @u_int16_t int
 * @Out @u_int32_t NativeLongByReference  -> @Out @u_int32_t long
 */
public interface CAPIServiceLib {
    /**
     * Load library
     * @return CAPIServiceLib instance
     */
    static CAPIServiceLib load() {
        return LibraryLoader
                .create(CAPIServiceLib.class)
                .option(LibraryOption.LoadNow, true)
                .option(LibraryOption.SaveError, true)
                .failImmediately()
                .search("/home/kryo/Desktop/meshchat/client/src/main/resources")
                .load("cli_service");
    }

//    // define api
    int connect_server(@In String addr, @u_int16_t int port);
    int get_sockfd();
    void close_conn();
//
     int get_auth(@Out CharSequence _token, @Out @u_int32_t NativeLongByReference _uid);
     int is_authenticated();
     @u_int32_t long _get_uid();
     CharSequence _get_token();
////
     int _login(@In int sockfd, @In String  username, @In String password);
     int _register(@In int sockfd, @In RequestAuth req);
     int _logout(@In int sockfd);

     int _get_user_info(@In int sockfd, @u_int32_t long user2_id, @Out ResponseUser _res);
     int _get_user_search(@In int sockfd, @In CharSequence uname, @In @int32_t long offset, @In @int32_t long limit, @Out @u_int32_t long[] _idls, @Out @u_int32_t NativeLongByReference _len);
     int _create_conv(@In int sockfd, @In CharSequence gname, @Out @u_int32_t NativeLongByReference _gid);
     int _drop_conv(@In int sockfd, @In @u_int32_t long conv_id);
     int _join_conv(@In int sockfd, @In @u_int32_t long conv_id, @In @u_int32_t long user2_id);
     int _quit_conv(@In int sockfd, @In @u_int32_t long conv_id);
     int _get_conv_info(@In int sockfd, @In @u_int32_t long conv_id, @Out @u_int32_t NativeLongByReference _admin_id, @Out CharSequence _gname);
     int _get_conv_members(@In int sockfd, @In @u_int32_t long conv_id, @Out @u_int32_t NativeLongByReference _res, @Out @u_int32_t NativeLongByReference _len);
     int _get_conv_list(@In int sockfd, @In @int32_t long limit, @In @int32_t long offset, @Out @u_int32_t long[] _idls, @Out @u_int32_t NativeLongByReference _len);
     int _create_chat(@In int sockfd, @In @u_int32_t long user2_id, @Out @u_int32_t NativeLongByReference  chat_id);
     int _delete_chat(@In int sockfd, @In @u_int32_t long chat_id);
     int _get_chat_list(@In int sockfd, @In @int32_t long limit, @In @int32_t long offset, @Out @u_int32_t long[] _idls, @Out @u_int32_t NativeLongByReference _len);
     int _get_msg_all(@In int sockfd, @In @int32_t long limit, @In @int32_t long offset, @In @u_int32_t long conv_id, @In @u_int32_t long  chat_id, @Out @u_int32_t long[] _msg_idls, @Out @u_int32_t NativeLongByReference _len);
//     int _get_msg_detail(@In int sockfd, @In @u_int32_t long msg_id, response_msg *_msg);
     int _send_msg_text(@In int sockfd, @In @u_int32_t long conv_id, @In @u_int32_t long chat_id, @In @u_int32_t long reply_to, @In CharSequence msg, @Out @u_int32_t NativeLongByReference _msg_id);
     int _delete_msg(@In int sockfd, @In @u_int32_t long msg_id);
     int _notify_new_msg(@In int sockfd, @In @u_int32_t long user_id, @Out @u_int32_t long[] _idls, @Out @u_int32_t NativeLongByReference _len);
     int _notify_del_msg(@In int sockfd, @In @u_int32_t long conv_id, @In @u_int32_t long chat_id, @Out @u_int32_t long[] _idls, @Out @u_int32_t NativeLongByReference _len);
}
