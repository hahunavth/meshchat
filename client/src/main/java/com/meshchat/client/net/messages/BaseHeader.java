package com.meshchat.client.net.messages;

import com.meshchat.client.net.OpGroup;
import com.meshchat.client.net.OpSubGroup;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class BaseHeader extends Struct {

    private OpGroup group;
    private OpSubGroup subGroup;

    protected BaseHeader(Runtime runtime) {
        super(runtime);
    }

    public OpGroup getGroup() {
        return group;
    }

    public void setGroup(OpGroup group) {
        this.group = group;
    }

    public OpSubGroup getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(OpSubGroup subGroup) {
        this.subGroup = subGroup;
    }
}
