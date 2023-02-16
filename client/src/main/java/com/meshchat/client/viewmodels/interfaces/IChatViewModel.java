package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import javafx.collections.ObservableList;

public interface IChatViewModel {
    void fetchChatList();
    void fetchConvList();
    ObservableList<Chat> getChatList();
    ObservableList<Conv> getConvList();
}
