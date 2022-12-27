package com.meshchat.client.net;

import com.meshchat.client.model.Message;
import com.meshchat.client.net.messages.BaseParser;
import javafx.beans.InvalidationListener;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * Connection with server
 * @see com.meshchat.client.net.providers.ApiProvider
 * @see com.meshchat.client.model.DataSource
 */
public class TCPClient extends SubmissionPublisher<char[]> implements Runnable, Flow.Publisher<char[]> {
    // constant
    public final int BUFF_SIZE = 8192;
    // socket
    private Socket socket;
    // writer, reader
    private PrintWriter writer;
    private BufferedReader reader;
    // address
    private final String host;
    private final int port;
    // state
    private boolean isConnected;
    private List<Flow.Subscriber> subscriberList = new ArrayList<>();

    public TCPClient() {
        this("127.0.0.1", 5500);
    }

    public TCPClient(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    private void connect () {
        // connect
        while (socket == null) {
            try {
                socket = new Socket(host, port);
                // io stream
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                //
                writer = new PrintWriter(outputStream, true);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //
                reader = new BufferedReader(inputStreamReader);
                this.setConnected(true);
                System.out.println("Connect successfully!");
            } catch (IOException e) {
                System.out.println( e.getMessage() + ": Try reconnect in 5 second!");
                this.setConnected(false);
                // wait 5 second and reconnect
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Override
    public void run() {
        // connect
        connect();

        // communication
        while (socket != null) {
            char[] frame = receive();
            // TODO: handle command with c code (event/action)
            System.out.println("Recv: " + frame.length + " bytes");
            System.out.println("Data: " + Arrays.toString(frame));

            this.subscriberList.forEach(subscriber -> {
                subscriber.onNext(frame);
            });

        }
    }

    private char[] receive () {
        char[] buffer = new char[BUFF_SIZE + 1];
        try {
            int recvBytes = reader.read(buffer, 0, BUFF_SIZE);
            buffer[recvBytes] = '\0';
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    public void send(String s) {
        writer.println(s);
    }

    public void send(byte[] bytes) {
        writer.println(Arrays.toString(bytes));
    }

    public void send(BaseParser m) {

    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void close() {
        super.close();
        if(socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void subscribe(Flow.Subscriber subscriber) {
        super.subscribe(subscriber);
        subscriberList.add(subscriber);

        Flow.Subscription subscription = new Flow.Subscription() {
            @Override
            public void request(long n) {

            }

            @Override
            public void cancel() {

            }
        };

        subscriber.onSubscribe(subscription);
    }
}
