package com.meshchat.client.net.providers;

import com.meshchat.client.net.*;

import java.util.*;

public class ApiProvider implements IApiProvider {
    private TCPClient client;
    private final Queue<ApiResponseHandler> apiResponseHandlers = new PriorityQueue<>();
    private final Map<OpGroup, IOpApiProvider> opApiProviderMap = new HashMap<>();

    public ApiProvider(TCPClient client) {
        this.client = client;
        opApiProviderMap.put(OpGroup.AUTH, new ApiAuthProvider(client));
    }

    public void request(OpGroup group, OpSubGroup subGroup) throws Exception {
        IOpApiProvider provider = opApiProviderMap.getOrDefault(group, null);
        if (group == null) {
            throw new Exception("Unsupported message found");
        } else {
            provider.request(subGroup);
        }
    }

}
