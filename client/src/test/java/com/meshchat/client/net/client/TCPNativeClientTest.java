package com.meshchat.client.net.client;

import com.google.inject.Inject;
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
    public TCPNativeClient client = new TCPNativeClient("127.0.0.1", 9000);
    @BeforeEach
    void setUp() {
        client.initClient("127.0.0.1", 9000);
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

    @Test
    void testSearchUserList () throws APICallException {
        long [] ls = this.client._get_user_search("user", 10, 0);
        System.out.println(ls);
    }

    @Test
    void testGetConvInfo () throws APICallException {
        this.client._get_conv_info(1);
    }

    @Test
    void testNotifyNew() throws APICallException {
        List<Long> ls1 = this.client.notifyNewMsg();

        System.out.println("Msg id: " + ls1.get(1));
        System.out.println(this.client._get_msg_detail(ls1.get(1)));

        List<Long> ls2 = this.client.notifyNewMsg();
        assertNotEquals(ls1, ls2);
    }

    @Test
    void testNotifyDelete() throws APICallException {
        this.client.deleteMsg(2);
        System.out.println(this.client._get_msg_detail(1));
        List<Long> ls1 = this.client.notifyDeleteMsg(ChatRoomType.CHAT, 1);
        System.out.println(ls1);
        assertFalse(ls1.contains(1L));
    }

}