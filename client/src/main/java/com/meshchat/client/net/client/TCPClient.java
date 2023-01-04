package com.meshchat.client.net.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * Connection with server
// * @see com.meshchat.client.net.providers.ApiProvider
 * @see com.meshchat.client.model.DataSource
 */
public abstract class TCPClient extends SubmissionPublisher<char[]> implements Runnable, Flow.Publisher<char[]> {
    // constant
    public final int DEFAULT_BUFF_SIZE = 8192;
    protected int buff_size = DEFAULT_BUFF_SIZE;

    // address
    protected final String host;
    protected final int port;
    // state
    private boolean isConnected;
    protected List<Flow.Subscriber> subscriberList = new ArrayList<>();

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
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
}
