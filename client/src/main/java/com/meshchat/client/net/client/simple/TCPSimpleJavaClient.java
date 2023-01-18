package com.meshchat.client.net.client.simple;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * @deprecated
 */
public class TCPSimpleJavaClient extends TCPSimpleClient {
    // socket
    private Socket socket;
    // writer, reader
    private PrintWriter writer;
    private BufferedReader reader;

    public TCPSimpleJavaClient() {
        this("127.0.0.1", 5500);
    }

    public TCPSimpleJavaClient(String host, int port) {
        super(host, port);
    }

    protected void connect () {
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

    protected char[] receive () {
        char[] buffer = new char[buff_size];
        try {
            int recvBytes = reader.read(buffer, 0, buff_size);
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

    public void close() {
        if(socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
