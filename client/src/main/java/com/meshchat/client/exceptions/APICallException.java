package com.meshchat.client.exceptions;

public class APICallException extends Exception{
    private int statusCode;

    public APICallException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return "[" + this.statusCode + "]" + super.getMessage();
    }
}
