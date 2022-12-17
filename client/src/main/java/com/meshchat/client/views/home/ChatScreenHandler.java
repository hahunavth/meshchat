package com.meshchat.client.views.home;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatScreenHandler extends BaseScreenHandler {

    @FXML
    private VBox chatList;

    public ChatScreenHandler(String fxmlPath) throws IOException {
        super(fxmlPath);
    }

    public ChatScreenHandler(Stage stage) throws IOException {
        super(stage, Config.CHAT_LIST_PATH);
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
        addChatItem(new ChatItem(stage));
    }

    public void addChatItem (FXMLScreenHandler screenHandler) {
        this.chatList.getChildren().add(screenHandler.getContent());
    }
}
