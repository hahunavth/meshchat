package com.meshchat.client;

import com.google.inject.Inject;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.net.client.TCPNativeClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Seed {

    @Inject
    TCPNativeClient client;
    @BeforeEach
    void setUp() {
        client.initClient("127.0.0.1", 9000);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    /**
     * Create account: <br>
     * username: user_* <br>
     * password: pass* <br>
     * 0 < * < 100 <br>
     */
    @Test
    void createAcc () {
        boolean ret;
        for(int i = 0; i < 100; i++) {
            ret = client._register("user_" + i, "pass" + i, "0987654321", "a" + i + "@b.c");
            assertTrue(ret);
        }
    }

    /**
     * Create chat: <br>
     * user_1 -> user_2 <br>
     * user_1 -> user_3 <br>
     */
    @Test
    void createChat() {
        // login
        boolean ret;
        ret = client._login("user_1", "pass1");
        assertTrue(ret);
        long u1id = client.get_uid();
        assertTrue(u1id > 0);
        // user_1 chat with user_2 and user_3
        try {
            long chat_id = client._create_chat(u1id + 1);
            assertTrue(chat_id > 0);
            chat_id = client._create_chat(u1id + 2);
            assertTrue(chat_id > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void createChat2() {
        boolean ret;
        int u1id = 1;
        ret = client._login("user_4", "pass4");
        assertTrue(ret);
        long u4id = client.get_uid();
        assertTrue(u4id > 0);
        try {
            long chat_id = client._create_chat(u1id);
            assertTrue(chat_id > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void createMsg() {
        boolean ret;
        int u1id = 1;
        ret = client._login("user_4", "pass4");
        assertTrue(ret);
        long u4id = client.get_uid();
        assertTrue(u4id > 0);
        try {
            long chat_id = 3;
            this.client._send_msg(ChatRoomType.CHAT, chat_id, 0, "Hello");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
