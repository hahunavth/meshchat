package com.meshchat.client.net.client;

import com.meshchat.client.controllers.MessageController;
import com.meshchat.client.experiments.libs.TypeMappingLib;
import javafx.beans.Observable;

import java.util.Arrays;

public class TCPSimpleCClient extends TCPClient {
    private final TypeMappingLib lib;
    public TCPSimpleCClient(String host, int port, TypeMappingLib lib) {
        super(host, port);
        this.lib = lib;
//        this.lib.init();
    }

    @Override
    protected void connect() {
        int fd;
        do {
            fd = lib.connect_server(this.host, this.port);
            if (fd == -1) {
                System.out.println("Try reconnect in 3 second!");
                // wait 3 second and reconnect
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (fd == -1);
        System.out.println("Connect successfully!");
    }

    @Override
    public void send(String s) {
        this.lib.simple_send(s);
    }

    @Override
    public void send(byte[] bytes) {
        this.lib.simple_send(Arrays.toString(bytes));
    }

    @Override
    protected char[] receive() {
        return this.lib.simple_recv().toCharArray();
    }

    @Override
    public void close() {
        this.lib.close_conn();
    }

    @Override
    public void run() {
        try {
            connect();
            while(true) {
                if(this.lib.get_sockfd() == -1) {
                    System.out.println("reconnect");
                    connect();
                } else {
                    System.out.println(receive());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
