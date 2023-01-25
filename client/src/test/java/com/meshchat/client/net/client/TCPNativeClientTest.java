package com.meshchat.client.net.client;

import com.meshchat.client.ModelSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NOTE: run seed first
 */
class TCPNativeClientTest {
    TCPNativeClient client;
    @BeforeEach
    void setUp() {
        client = ModelSingleton.getInstance().tcpClient;
        ModelSingleton.getInstance().initClient("127.0.0.1", 9000);
        client._login("user_1", "pass1");
    }

    @Test
    void _get_chat_list () {
        List<Long> chatList = client._get_chat_list();
        System.out.println(chatList);
    }
}