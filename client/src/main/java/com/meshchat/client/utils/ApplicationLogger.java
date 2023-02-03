package com.meshchat.client.utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.meshchat.client.exceptions.APICallException;

import java.util.logging.*;

@Singleton
public class ApplicationLogger implements BaseLogger {

    private final Logger logger;

    @Inject
    public ApplicationLogger(Logger logger) {
        this.logger = logger;
    }

    public void logApiCallException(APICallException e) {
        logger.warning(e.getMessage());
    }
}