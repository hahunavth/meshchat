package com.meshchat.client.net;

import com.meshchat.client.net.providers.IApiProvider;

public enum OpSubGroup implements IApiProvider {
    REGISTER,
    LOGIN,
    LOGOUT,
    GET_USER_INFO,
    SEARCH_USER,
    CREATE,
    DROP,
    JOIN,
    QUIT,
    GET_INFO,
    GET_MEMBERS,
    GET_CONVERSATION_LIST,
    GET_CHAT_LIST,
    //
    GET_ALL,
    GET_ONE,
    // todo...
}
