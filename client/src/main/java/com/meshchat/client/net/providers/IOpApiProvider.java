package com.meshchat.client.net.providers;

import com.meshchat.client.net.OpGroup;
import com.meshchat.client.net.OpSubGroup;

public interface IOpApiProvider {
    public OpGroup getOpGroup ();
    void request(OpSubGroup subGroup);
}
