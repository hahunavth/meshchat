package com.meshchat.client.net.messages;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class BaseBody extends Struct {
    protected BaseBody(Runtime runtime) {
        super(runtime);
    }
}
