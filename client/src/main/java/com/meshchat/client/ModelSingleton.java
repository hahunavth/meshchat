package com.meshchat.client;

import com.meshchat.client.experiments.libs.TypeMappingLib;
import com.meshchat.client.model.DataSource;
import com.meshchat.client.net.client.simple.TCPSimpleClient;
import com.meshchat.client.net.client.simple.TCPSimpleCClient;
import com.meshchat.client.views.navigation.StackNavigation;
import jnr.ffi.LibraryLoader;
import jnr.ffi.LibraryOption;

/**
 * Singleton:
 * - tcpClient
 * - apiProvider
 * - dataSource
 */
public class ModelSingleton {
    private static ModelSingleton instance;

    //
    public TypeMappingLib lib;
    public final DataSource dataSource = new DataSource();
    public TCPSimpleClient tcpClient;
    public StackNavigation stackNavigation;;

    private ModelSingleton() {
        lib = LibraryLoader
                .create(TypeMappingLib.class)
                .option(LibraryOption.LoadNow, true)
                .option(LibraryOption.SaveError, true)
                .failImmediately()
                .search("/home/kryo/Desktop/meshchat/client/src/main/resources")
                .load("typemapping");

        tcpClient = new TCPSimpleCClient(lib);
        // close on exit
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
            tcpClient.close();
            System.out.println("Close connection!");
        }});

        tcpClient.subscribe(dataSource);
    }

    public void initClient(String host, int port) {
        this.tcpClient.setCloseFlag(false);
        this.tcpClient.setHost(host);
        this.tcpClient.setPort(port);
        Thread clientThread = new Thread(tcpClient);
        clientThread.start();
    }

    public static ModelSingleton getInstance() {
        if(instance == null) {
            synchronized(ModelSingleton.class) {
                if(null == instance) {
                    instance  = new ModelSingleton();
                }
            }
        }
        return instance;
    }

    public void close () {
        if (this.tcpClient != null) {
            this.tcpClient.close();
        }
    }
}
