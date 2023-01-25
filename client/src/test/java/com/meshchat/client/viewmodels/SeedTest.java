package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.net.client.TCPNativeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeedTest {

    TCPNativeClient client;
    @BeforeEach
    void setUp() {
        client = ModelSingleton.getInstance().tcpClient;
        ModelSingleton.getInstance().initClient("127.0.0.1", 9000);
    }

    @Test
    void createAcc () {
        boolean ret;
        for(int i = 0; i < 100; i++) {
            ret = client._register("user_" + i, "pass" + i, "0987654321", "a" + i + "@b.c");
            assertTrue(ret);
        }
    }

    @Test
    void createChat() {
        // login
        boolean ret;
        ret = client._login("user_1", "pass1");
        assertTrue(ret);
        long uid = client.get_uid();
        assertTrue(uid > 0);
        // user_1 chat with user_2 and user_3
        try {
            long chat_id = client._create_chat(uid + 1);
            assertTrue(chat_id > 0);
            chat_id = client._create_chat(uid + 2);
            assertTrue(chat_id > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
