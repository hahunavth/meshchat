package com.meshchat.client.cnative.req;

import com.meshchat.client.utils.Config;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;


/**
 * typedef struct
 * {
 * 	char *uname;
 * 	char *password;
 * 	char *phone;
 * 	char *email;
 * } request_auth;
 */
public class RequestAuth extends Struct {
    public Struct.String uname = new Struct.AsciiStringRef(8192);

    public Struct.String password = new Struct.AsciiStringRef(8192);
    public Struct.String phone = new Struct.AsciiStringRef(8192);
    public Struct.String email = new Struct.AsciiStringRef(8192);


    public RequestAuth(Runtime runtime) {
        super(runtime);
    }
}
