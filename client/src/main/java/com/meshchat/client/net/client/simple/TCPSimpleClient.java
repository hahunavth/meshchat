package com.meshchat.client.net.client.simple;

import com.meshchat.client.model.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * @deprecated
 * Connection with server
 * @see DataStore
 */
public abstract class TCPSimpleClient extends SubmissionPublisher<char[]> implements Runnable, Flow.Publisher<char[]> {
    // constant
    public final int DEFAULT_BUFF_SIZE = 8192;
    protected int buff_size = DEFAULT_BUFF_SIZE;

    // address
    protected String host;
    protected int port;
    // state
    private boolean isConnected;
    private boolean closeFlag = false;

    protected List<Flow.Subscriber> subscriberList = new ArrayList<>();

    public TCPSimpleClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public TCPSimpleClient() {
    }

    // getter, setter
    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public int getBuffSize() {
        return buff_size;
    }

    public void setBuffSize(int buff_size) {
        this.buff_size = buff_size;
    }

    // SubmissionPublisher
    @Override
    public void subscribe(Flow.Subscriber subscriber) {
        super.subscribe(subscriber);
        subscriberList.add(subscriber);

        Flow.Subscription subscription = new Flow.Subscription() {
            @Override
            public void request(long n) {
                System.out.println(n);
            }

            @Override
            public void cancel() {
                System.out.println("Cancel");
            }
        };

        subscriber.onSubscribe(subscription);
    }

    // common net client
    protected abstract void connect ();
    public abstract void send(String s);
    public abstract void send(byte[] bytes);
    protected abstract char[] receive ();
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
    }

    public boolean isCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(boolean closeFlag) {
        this.closeFlag = closeFlag;
    }
}
