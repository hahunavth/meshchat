package com.meshchat.client.cnative.res;

import com.meshchat.client.utils.Config;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

/**
 * typedef struct
 * {
 * 	uint32_t *idls;
 * 	char *uname;
 * 	char *phone;
 * 	char *email;
 * } response_user;
 */
public class ResponseUser extends Struct {

    /**
     * Giữ attribute này để đúng với struct trong C,
     * Tránh ảnh hưởng đến việc map type
     */
    @Deprecated
    private final Struct.u_int32_t idls = new u_int32_t();
    public Struct.String uname = new Struct.AsciiStringRef(Config.NORMAL_STR_SZ);
    public Struct.String phone = new Struct.AsciiStringRef(Config.NORMAL_STR_SZ);
    public Struct.String email = new Struct.AsciiStringRef(Config.NORMAL_STR_SZ);
    public ResponseUser(Runtime runtime) {
        super(runtime);
    }

    @Override
    public java.lang.String toString() {
        return "ResponseUser{" +
                "uname=" + uname +
                ", phone=" + phone +
                ", email=" + email +
                '}';
    }
}
