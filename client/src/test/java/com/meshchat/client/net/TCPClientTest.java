//package com.meshchat.client.net;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.concurrent.Flow;
//
//class TCPClientTest {
//
//    private TCPSimpleClient client;
//    private static boolean WAIT_FLAG = true;
//
//    @BeforeEach
//    void setUp() {
//        WAIT_FLAG = true;
//
//        client = new TCPSimpleJavaClient("127.0.0.1", 5500);
//        client.setBuffSize(4);
//        Thread thread = new Thread(client);
//        thread.start();
//    }
//
//    @AfterEach
//    void tearDown() {
//        client.close();
//    }
//
//    void waitConnection(int timeout) {
//        for (int i = 0; i < timeout; i++) {
//            System.out.println("WaitConn: " + i + ", status: " + client.isConnected());
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (client.isConnected()) {
//                break;
//            }
//        }
//    }
//    void waitFlag(int timeout) {
//        for (int i = 0; i < timeout; i++) {
//            System.out.println("WaitFlag: " + i + ", status: " + WAIT_FLAG);
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (!WAIT_FLAG) {
//                break;
//            }
//        }
//    }
//
//    void stopWait() {
//        WAIT_FLAG = false;
//    }
//
//    @Test
//    void testSend(){
//        waitConnection(5);
//        client.send("Hello");
//    }
//
//    @Test
//    void testRecv() {
//        waitConnection(10);
//        TCPClientSubscriber subscriber = new TCPClientSubscriber();
//        client.subscribe(subscriber);
//        waitFlag(30);
//    }
//
//    @Test
//    void subscribe() {
//    }
//
//    /**
//     * test class handle response
//     */
//    class TCPClientSubscriber implements Flow.Subscriber<char[]> {
//
//        SimpleStringMultiPartBufferReceiver bufferReceiver;
//
//        @Override
//        public void onSubscribe(Flow.Subscription subscription) {
//            subscription.request(1);
//            System.out.println("Subscribe");
//        }
//
//        @Override
//        public void onNext(char[] item) {
//
//            if (bufferReceiver == null) {
//                bufferReceiver = new SimpleStringMultiPartBufferReceiver();
//            }
//
//            System.out.println("Next");
//            // handle response here
//            System.out.println(item);
//
//            bufferReceiver.joinBodyBuff(bufferReceiver.splitBody(item));
//
//            if (bufferReceiver.isFinallyPart(item)) {
//                System.out.println(
//                        bufferReceiver.getBody()
//                );
//                bufferReceiver = null;
//                stopWait();
//            }
//        }
//
//        @Override
//        public void onError(Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        @Override
//        public void onComplete() {
//            System.out.println("Completed");
//        }
//    }
//}