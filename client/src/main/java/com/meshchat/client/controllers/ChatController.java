package com.meshchat.client.controllers;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.DataSource;
import com.meshchat.client.model.User;
import com.meshchat.client.views.home.ChatScreenHandler;
import javafx.collections.MapChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChatController extends BaseController<ChatScreenHandler> {
    public List<Long> getChatIdList() {
        List<Long> list = new ArrayList<>(dataSource.getChatMap().keySet());
        Collections.sort(list);
        return list;
    }

    public Map<Long, Chat> getChatMap() {
        return dataSource.getChatMap();
    }

    @Override
    public void setScreenHandler(ChatScreenHandler screenHandler) {
        super.setScreenHandler(screenHandler);

        // add item
        ModelSingleton.getInstance().dataSource.oChatMap.forEach((aLong, chat) -> {
            this.getScreenHandler().addChatItem(aLong, chat.getUser2().getUsername(), "", MessageController.Type.CHAT);
        });

        // add handler
        ModelSingleton.getInstance().dataSource.oChatMap.addListener((MapChangeListener<? super Long, ? super Chat>) (e) -> {
            Long key = e.getKey();
            if (e.wasAdded()) {
                Chat chat = e.getValueAdded();
                this.getScreenHandler().addChatItem(key, chat.getUser2().getUsername(), "", MessageController.Type.CHAT);
            } else if (e.wasRemoved()) {
                this.getScreenHandler().removeChatItem(key, MessageController.Type.CHAT);
            }

            System.out.println(key);
        });

        ModelSingleton.getInstance().dataSource.oChatMap.put(345L, new Chat(new User()));
    }
}
