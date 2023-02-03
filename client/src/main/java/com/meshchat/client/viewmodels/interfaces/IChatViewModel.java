package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.model.Chat;
import javafx.collections.ObservableMap;

public interface IChatViewModel {
    void fetchChatList();
    void fetchConvList();
    ObservableMap<Long, Chat> getChatMap();
}
