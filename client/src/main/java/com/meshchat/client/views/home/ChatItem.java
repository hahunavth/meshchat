package com.meshchat.client.views.home;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatItem extends BaseScreenHandler {
    @FXML
    private HBox item;
    public ChatItem(Stage stage) {
        super(stage, Config.CHAT_ITEM_PATH);
    }

    @FXML
    public void initialize() {
        item.setOnMouseClicked((event) -> {
            System.out.println("Chat item");
        });
    }
}
