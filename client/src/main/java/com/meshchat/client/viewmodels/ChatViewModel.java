package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IChatViewModel;
import javafx.collections.ObservableMap;

import java.util.List;

public class ChatViewModel extends BaseViewModel implements IChatViewModel {

    @Inject
    public ChatViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public ObservableMap<Long, Chat> getChatMap() {
        return this.getDataStore().getOChatMap();
    }

    /**
     * Fetch chat list and add to DataStore
     */
    @Override
    public void fetchChatList() {
        List<Long> chatIdls = this.getTcpClient()._get_chat_list();
        System.out.println("Chat list: " + chatIdls);
        this.getDataStore().getOChatMap().forEach((i, c) -> {
            this.getDataStore().getOChatMap().remove(i);
        });
        chatIdls.forEach((chatId) -> {
            try {
                Chat chat = this.getTcpClient()._get_chat_info(chatId);
                this.getDataStore().addChat(chatId, chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void fetchConvList() {
        List<Long> convIdls = this.getTcpClient()._get_conv_list();
        System.out.println("conv list: " + convIdls);
        this.getDataStore().getOConvMap().forEach((i, c) -> {
            this.getDataStore().getOConvMap().remove(i);
        });
        convIdls.forEach((chatId) -> {
            try {
                Conv conv = this.getTcpClient()._get_conv_info(chatId);
                this.getDataStore().addConv(chatId, conv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
