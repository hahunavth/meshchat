package com.meshchat.client.net.client;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.db.entities.MsgEntity;
import com.meshchat.client.exceptions.APICallException;
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
        client._login("user_1", "pass");
    }

    @Test
    void _get_chat_list () {
        List<Long> chatList = client._get_chat_list();
        System.out.println(chatList);
    }

    @Test
    void testGetMsgDetailRaw() throws APICallException {
        MsgEntity msg = this.client._get_msg_detail(3);
        System.out.println(msg);
    }
}