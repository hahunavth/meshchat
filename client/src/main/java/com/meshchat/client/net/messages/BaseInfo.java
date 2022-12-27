package com.meshchat.client.net.messages;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class BaseInfo extends Struct {
    protected BaseInfo(Runtime runtime) {
        super(runtime);
    }

    private BaseHeader header;

    private BaseBody body;

    public BaseHeader getHeader() {
        return header;
    }

    public void setHeader(BaseHeader header) {
        this.header = header;
    }

    public BaseBody getBody() {
        return body;
    }

    public void setBody(BaseBody body) {
        this.body = body;
    }
}
