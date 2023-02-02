package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Chat;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.net.client.TCPNativeClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatViewModelTest {

    TCPNativeClient client;
    ChatViewModel viewModel;

    @BeforeEach
    void setUp() {
        client = ModelSingleton.getInstance().tcpClient;
        viewModel = new ChatViewModel();

        ModelSingleton.getInstance().initClient("127.0.0.1", 9000);
        client._login("user_2", "pass");
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void fetchChatList() {
        viewModel.fetchChatList();
        assertTrue(viewModel.dataStore.getOChatMap().size() > 0);
    }

    @Test
    void clientGetChatInfoTest() {
        List<Long> chatIdls = client._get_chat_list();
        System.out.println("Chat list: " + chatIdls);
        chatIdls.forEach((chatId) -> {
            try {
                Chat chat = client._get_chat_info(chatId);
                System.out.println(chat);
            } catch (APICallException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void clientChatGetAllMsgTest () {
//        List<Long> chatIdls = client._get_chat_list();
//        System.out.println("Chat list: " + chatIdls);
//        chatIdls.forEach((chatId) -> {
            try {
//                Chat chat = client._get_chat_info(chatId);
//                System.out.println(chat);
                int chatId = 4;
                List<Long> msgls = client._get_msg_all(ChatRoomType.CHAT, 1, 10, 0);
                System.out.println(msgls);
            } catch (APICallException e) {
                e.printStackTrace();
            }
//        });
    }
}