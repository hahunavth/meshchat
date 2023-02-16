package com.meshchat.client.observablebinding;

import com.meshchat.client.model.Conv;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.views.components.ChatItem;
import com.meshchat.client.views.components.ChatItemChat;
import com.meshchat.client.views.components.ChatItemConv;
import com.meshchat.client.views.home.MessageScreenHandler;
import javafx.collections.ObservableList;

public class ConvToChatItemConvBinding extends CustomUIBinding<Conv, ChatItem>{

    MessageScreenHandler screenHandler;
    public ConvToChatItemConvBinding(ObservableList<ChatItem> listOut, MessageScreenHandler screenHandler) {
        super(listOut);
        this.screenHandler = screenHandler;
    }

    @Override
    public ChatItemConv convert(Conv chatRoom) {
        ChatItemConv chatItem = new ChatItemConv(chatRoom);
        chatItem.onClick((e) -> {
            this.screenHandler.getViewModel().setRoomInfo(ChatRoomType.CONV, chatRoom.id, chatRoom);
        });
        return chatItem;
    }
}
