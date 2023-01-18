package com.meshchat.client.viewmodels;

import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChatViewModel extends BaseViewModel {
    public List<Long> getChatIdList() {
        List<Long> list = new ArrayList<>(dataStore.getChatMap().keySet());
        Collections.sort(list);
        return list;
    }

    public Map<Long, Chat> getChatMap() {
        return dataStore.getChatMap();
    }

    public Map<Long, Conv> getConvMap () {
        return this.dataStore.getConvMap();
    }

//    @Override
//    public void setScreenHandler(ChatScreenHandler screenHandler) {
//        super.setScreenHandler(screenHandler);
//
//        // add item
//        ModelSingleton.getInstance().dataSource.oChatMap.forEach((aLong, chat) -> {
//            this.getScreenHandler().addChatItem(aLong, chat.getUser2().getUsername(), "", MessageViewModel.Type.CHAT);
//        });
//
//        // add handler
//        ModelSingleton.getInstance().dataSource.oChatMap.addListener((MapChangeListener<? super Long, ? super Chat>) (e) -> {
//            Long key = e.getKey();
//            if (e.wasAdded()) {
//                Chat chat = e.getValueAdded();
//                this.getScreenHandler().addChatItem(key, chat.getUser2().getUsername(), "", MessageViewModel.Type.CHAT);
//            } else if (e.wasRemoved()) {
//                this.getScreenHandler().removeChatItem(key, MessageViewModel.Type.CHAT);
//            }
//
//            System.out.println(key);
//        });

//        ModelSingleton.getInstance().dataSource.oChatMap.put(345L, new Chat(new UserEntity()));
//    }
}
