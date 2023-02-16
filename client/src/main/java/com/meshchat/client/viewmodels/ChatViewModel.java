package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IChatViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;

public class ChatViewModel extends BaseViewModel implements IChatViewModel {
    private final ObservableList<Chat> oChatMap = FXCollections.observableArrayList();
    private final ObservableList<Conv> oConvMap = FXCollections.observableArrayList();

    @Inject
    public ChatViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public ObservableList<Chat> getChatList() {
        return this.oChatMap;
    }

    public ObservableList<Conv> getConvList() {
        return this.oConvMap;
    }

    /**
     * Fetch chat list and add to DataStore
     */
    @Override
    public void fetchChatList() {
//        this.oChatMap.forEach((i, j) -> {
//            Platform.runLater(() -> {
//                this.oChatMap.remove(i);
//            });
//        });
//        Platform.runLater(() -> {
            this.oChatMap.clear();
//        });

        List<Long> chatIdls = new ArrayList<>(this.getTcpClient()._get_chat_list());
        chatIdls.sort(Comparator.reverseOrder());
        System.out.println("Chat list: " + chatIdls);

        chatIdls.forEach((chatId) -> {
            try {
                Chat chat = this.getTcpClient()._get_chat_info(chatId);
                Platform.runLater(() -> {
                    this.oChatMap.add(chat);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void fetchConvList() {
//        try{
//            this.oConvMap.forEach((i, j) -> {
//                Platform.runLater(() -> {
//                    this.oConvMap.remove(i);
//                });
//            });
//        }catch(Exception e){e.printStackTrace();}

        List<Long> convIdls = new ArrayList<>(this.getTcpClient()._get_conv_list());
        convIdls.sort(Comparator.reverseOrder());
        System.out.println("conv list: " + convIdls);

        convIdls.forEach((chatId) -> {
            try {
                Conv conv = this.getTcpClient()._get_conv_info(chatId);
                this.oConvMap.add(conv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
