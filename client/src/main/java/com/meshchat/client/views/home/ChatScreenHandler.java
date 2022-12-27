package com.meshchat.client.views.home;

import com.meshchat.client.controllers.ChatController;
import com.meshchat.client.controllers.MessageController;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.FXMLScreenHandler;
import com.meshchat.client.views.components.ChatItem;
import com.meshchat.client.views.factories.MessageScreenFactory;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChatScreenHandler extends BaseScreenHandler<ChatController> {
    @FXML
    private VBox chatList;

    private MessageScreenHandler messageScreenHandler;
    private List<FXMLScreenHandler> chatItemList = new ArrayList<>();

    public ChatScreenHandler() {
        super(Config.CHAT_LIST_PATH);
    }

    @FXML
    public void initialize() {
    }

    @Override
    public void setBaseController(ChatController controller) {
        super.setBaseController(controller);
    }

    public void setMessageScreenHandler(MessageScreenHandler messageScreenHandler, MessageScreenFactory factory) {
        this.messageScreenHandler = messageScreenHandler;
    }

    public void addChatItem(Long room_id, String name, String lastMsg, MessageController.Type type) {
        ChatItem chatItem = new ChatItem();
        chatItem.setName(name);
        chatItem.setLastmsg(lastMsg);
        chatItem.setId(room_id);
        chatItem.setType(type);
        chatItem.setOnClick((e) -> {
            messageScreenHandler.getBaseController().setRoomInfo(type, room_id);
            System.out.println("Set " + room_id);
        });
        addChatItem(chatItem);
    }

    public void removeChatItem(Long room_id, MessageController.Type type) {
        for (int i = 0; i < chatItemList.size(); i++) {
            ChatItem item = (ChatItem) chatItemList.get(i);
            if (((ChatItem) item).getId().equals(room_id)  && ((ChatItem) item).getType() == type) {
                this.chatItemList.remove(i);
                this.chatList.getChildren().remove(i);
                break;
            }
        }
    }

    protected void addChatItem (FXMLScreenHandler screenHandler) {
        this.chatList.getChildren().add(screenHandler.getContent());
        this.chatItemList.add(screenHandler);
    }
}
