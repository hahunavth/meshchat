package com.meshchat.client.net.providers;

import com.meshchat.client.net.OpGroup;
import com.meshchat.client.net.OpSubGroup;
import com.meshchat.client.net.TCPClient;

public class ApiAuthProvider implements IOpApiProvider{

    private TCPClient client;
    public ApiAuthProvider(TCPClient client) {
        this.client = client;
    }

    @Override
    public OpGroup getOpGroup() {
        return OpGroup.AUTH;
    }

    @Override
    public void request(OpSubGroup subGroup) {

    }
}
