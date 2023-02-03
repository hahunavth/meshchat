//package com.meshchat.client;
//
//import com.meshchat.client.model.DataStore;
//import com.meshchat.client.net.client.TCPNativeClient;
//import com.meshchat.client.views.navigation.StackNavigation;
//
///**
// * Singleton:
// * - tcpClient
// * - apiProvider
// * - dataSource
// */
//public class ModelSingleton {
//    private static ModelSingleton instance;
//    public final DataStore dataStore = new DataStore();
//    public TCPNativeClient tcpClient;
//    public StackNavigation stackNavigation;;
//
//    private ModelSingleton() {
//        tcpClient = new TCPNativeClient("127.0.0.1", 9000);
//        // close on exit
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            tcpClient.close();
//        }));
//    }
//
//
//
//    public static ModelSingleton getInstance() {
//        if(instance == null) {
//            synchronized(ModelSingleton.class) {
//                if(null == instance) {
//                    instance  = new ModelSingleton();
//                }
//            }
//        }
//        return instance;
//    }
//}
