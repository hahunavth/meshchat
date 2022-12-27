package com.meshchat.client;

import com.meshchat.client.model.DataSource;
import com.meshchat.client.net.providers.ApiProvider;
import com.meshchat.client.net.TCPClient;

/**
 * Singleton:
 * - tcpClient
 * - apiProvider
 * - dataSource
 */
public class ModelSingleton {
    private static ModelSingleton instance;

    //
    public final DataSource dataSource = new DataSource();
    public ApiProvider apiProvider;
    public TCPClient tcpClient;

    private ModelSingleton() {
        tcpClient = new TCPClient();
        apiProvider = new ApiProvider(tcpClient);
        // close on exit
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
            tcpClient.close();
            System.out.println("Close connection!");
        }});

        tcpClient.subscribe(dataSource);

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
}
