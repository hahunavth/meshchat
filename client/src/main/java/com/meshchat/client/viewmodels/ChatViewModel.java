package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChatViewModel extends BaseViewModel {
//    public List<Long> getChatIdList() {
//        List<Long> list = new ArrayList<>(dataStore.getChatMap().keySet());
//        Collections.sort(list);
//        return list;
//    }

    public ObservableMap<Long, Chat> getChatMap() {
        return dataStore.getOChatMap();
    }

//    public Map<Long, Conv> getConvMap () {
//        return this.dataStore.getConvMap();
//    }

    /**
     * Fetch chat list and add to DataStore
     */
    public void fetchChatList() {
        List<Long> chatIdls = ModelSingleton.getInstance().tcpClient._get_chat_list();
        System.out.println("Chat list: " + chatIdls);
        chatIdls.forEach((chatId) -> {
            try {
                Chat chat = ModelSingleton.getInstance().tcpClient._get_chat_info(chatId);
                this.dataStore.addChat(chatId, chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
