package com.meshchat.client.cnative.res;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

/**
 * typedef struct
 * {
 * 	char *token;
 * 	uint32_t user_id;
 * } response_auth;
 */
public class ResponseAuth extends Struct {
    public Struct.String token = new Struct.AsciiStringRef();
    public Struct.u_int32_t user_id = new u_int32_t();
    protected ResponseAuth(Runtime runtime) {
        super(runtime);
    }
}
