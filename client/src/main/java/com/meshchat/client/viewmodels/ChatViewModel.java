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

    public ObservableMap<Long, Conv> getConvMap() {
        return this.getDataStore().getOConvMap();
    }

    /**
     * Fetch chat list and add to DataStore
     */
    @Override
    public void fetchChatList() {
        this.getDataStore().getOChatMap().forEach((i, j) -> this.getDataStore().getOChatMap().remove(i));

        List<Long> chatIdls = this.getTcpClient()._get_chat_list();
        System.out.println("Chat list: " + chatIdls);

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
        this.getDataStore().getOConvMap().forEach((i, j) -> this.getDataStore().getOConvMap().remove(i));

        List<Long> convIdls = this.getTcpClient()._get_conv_list();
        System.out.println("conv list: " + convIdls);

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
