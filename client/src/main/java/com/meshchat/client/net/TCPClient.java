package com.meshchat.client.net;

import com.meshchat.client.events.Subject;
import com.meshchat.client.model.BaseModel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.EventObject;

public class TCPClient implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader inFromServer;
    private final String host;
    private final int port;
    private boolean isConnected;
    private EventHandler msgHandler;

    public TCPClient() {
        this("127.0.0.1", 5500);
    }

    public TCPClient(String host, int port) {
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
                inFromServer = new BufferedReader(inputStreamReader);
                this.setConnected(true);
                System.out.println("Connect successfully!");
            } catch (IOException e) {
                System.out.println( e.getMessage() + ": Try reconnect in 5 second!");
                this.setConnected(false);
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
        connect();

        // communication
        while (socket != null) {
            char[] frame = receive();
            // TODO: handle command with c code (event/action)
            if (msgHandler != null) {
                msgHandler.handle(new Event(new String(frame).trim(), null, null));
            }
            System.out.println("Recv: " + frame.length + " bytes");
            System.out.println("Data: " + Arrays.toString(frame));
        }
    }

    private char[] receive () {
        char[] buffer = new char[1024];
        try {
            int recvBytes = inFromServer.read(buffer, 0, 13);
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

    public void send(BaseModel m) {

    }

    public void setMsgHandler(EventHandler msgHandler) {
        this.msgHandler = msgHandler;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
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
