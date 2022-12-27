package com.meshchat.client.exceptions;

import com.meshchat.client.net.OpGroup;
import com.meshchat.client.net.OpSubGroup;

public class UnsupportedOpException extends Exception{
    public OpGroup opGroup;
    public OpSubGroup subGroup;
    public String controllerClass;

    public UnsupportedOpException(String message, OpGroup opGroup, OpSubGroup subGroup, String controllerClass) {
        super(message);
        this.opGroup = opGroup;
        this.subGroup = subGroup;
        this.controllerClass = controllerClass;
    }
}
