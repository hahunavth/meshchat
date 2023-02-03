package com.meshchat.client.utils;

import com.meshchat.client.exceptions.APICallException;

public interface BaseLogger {
    void logApiCallException(APICallException exception);
}
