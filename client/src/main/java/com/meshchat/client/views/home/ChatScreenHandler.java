package com.meshchat.client.views.home;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;

public class ChatScreenHandler extends BaseScreenHandler {
    @FXML
    private VBox chatList;

    public ChatScreenHandler(String fxmlPath) throws IOException {
        super(fxmlPath);
    }

    public ChatScreenHandler(Stage stage) {
        super(stage, Config.CHAT_LIST_PATH);
    }

    @FXML
    public void initialize() {
        Platform.runLater(
                ( ) -> {
                    for (int i = 0; i < 10; i++) {
                        ChatItem chatItem = new ChatItem(stage);
                        addChatItem(chatItem);
                    }
                }
        );
    }

    public void addChatItem (FXMLScreenHandler screenHandler) {
        this.chatList.getChildren().add(screenHandler.getContent());
    }
}
