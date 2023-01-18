package com.meshchat.client.net.client.simple;

import com.meshchat.client.experiments.libs.TypeMappingLib;

import java.util.Arrays;

/**
 * @deprecated
 */
public class TCPSimpleCClient extends TCPSimpleClient {
    private final TypeMappingLib lib;
    public TCPSimpleCClient(String host, int port, TypeMappingLib lib) {
        super(host, port);
        this.lib = lib;
//        this.lib.init();
    }

    public TCPSimpleCClient(TypeMappingLib lib) {
        this.lib = lib;
    }

    @Override
    protected void connect() {
        int fd;
        do {
            if (this.isCloseFlag()) {
                this.setCloseFlag(false);
                return;
            }
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
        this.setConnected(true);
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
        if(this.isConnected()) {
            this.setConnected(false);
        } else {
            this.setCloseFlag(true);
        }
    }

    @Override
    public void run() {
        try {
            connect();
            if (isConnected()) {
                while(true) {
                    if(this.lib.get_sockfd() == -1) {
                        System.out.println("reconnect");
                        connect();
                    } else {
                        System.out.println(receive());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
