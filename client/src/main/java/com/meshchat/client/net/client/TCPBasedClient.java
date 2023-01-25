package com.meshchat.client.net.client;

public abstract class TCPBasedClient {
    public final int DEFAULT_BUFF_SIZE = 8192;
    // address
    protected String host;
    protected int port;
    // state
    private boolean isConnected;
    private boolean closeFlag = false;

    public TCPBasedClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    protected abstract void connect ();
    public abstract void close();

    public void setHost(String host) {
        if(this.isConnected) {
            throw new Error("Cannot set host when connecting");
        }
        this.host = host;
    }

    public void setPort (int port) {
        if (this.isConnected) {
            throw new Error("Cannot set port when connecting");
        }
        this.port = port;
    }

    public boolean isCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(boolean closeFlag) {
        this.closeFlag = closeFlag;
    }
}
