package com.meshchat.client;

import com.meshchat.client.model.DataSource;
import com.meshchat.client.net.TCPClient;
import com.meshchat.client.views.navigation.StackNavigation;

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
    public TCPClient tcpClient;
    public StackNavigation stackNavigation;;

    private ModelSingleton() {
        tcpClient = new TCPClient();
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
