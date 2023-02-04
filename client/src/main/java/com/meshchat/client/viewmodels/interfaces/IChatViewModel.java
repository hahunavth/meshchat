package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import javafx.collections.ObservableMap;

public interface IChatViewModel {
    void fetchChatList();
    void fetchConvList();
    ObservableMap<Long, Chat> getChatMap();
    ObservableMap<Long, Conv> getConvMap();
}
