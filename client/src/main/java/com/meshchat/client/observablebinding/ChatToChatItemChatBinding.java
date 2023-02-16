package com.meshchat.client.observablebinding;

import com.meshchat.client.model.Chat;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.views.components.ChatItem;
import com.meshchat.client.views.components.ChatItemChat;
import com.meshchat.client.views.home.MessageScreenHandler;
import javafx.collections.ObservableList;

public class ChatToChatItemChatBinding extends CustomUIBinding<Chat, ChatItem> {

    MessageScreenHandler screenHandler;
    public ChatToChatItemChatBinding(ObservableList<ChatItem> listOut, MessageScreenHandler screenHandler) {
        super(listOut);
        this.screenHandler = screenHandler;
    }

    @Override
    public ChatItemChat convert(Chat chatRoom) {
        ChatItemChat chatItem = new ChatItemChat(chatRoom);
        chatItem.onClick((e) -> {
            this.screenHandler.getViewModel().setRoomInfo(ChatRoomType.CHAT, chatRoom.id, chatRoom);
        });
        return chatItem;
    }
}
