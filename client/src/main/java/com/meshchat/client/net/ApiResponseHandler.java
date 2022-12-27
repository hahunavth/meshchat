package com.meshchat.client.net;

import com.meshchat.client.net.messages.BaseHeader;
import com.meshchat.client.net.messages.BaseInfo;

public interface ApiResponseHandler<T extends BaseInfo>{
    void handle(T info);

    boolean checkHeader(BaseHeader header);
}
