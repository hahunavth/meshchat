package com.meshchat.client.net.commands;

import com.meshchat.client.net.ApiResponseHandler;
import com.meshchat.client.net.OpGroup;
import com.meshchat.client.net.OpSubGroup;
import com.meshchat.client.net.messages.BaseInfo;

/**
 *
 */
public abstract class ApiCommand<T extends BaseInfo> {
    private OpGroup group;
    private OpSubGroup subGroup;

    abstract void send();
    abstract void onReceiveResponse(ApiResponseHandler<T> handler);

    abstract ApiResponseHandler<T> getApiResponseHandler ();
}
