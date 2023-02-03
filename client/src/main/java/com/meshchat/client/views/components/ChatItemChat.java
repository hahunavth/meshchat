package com.meshchat.client.views.components;

import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatItemChat extends ChatItem<Chat> {

    @FXML
    private Text name;
    @FXML
    private Text lastmsg;
    @FXML
    private HBox item;

    public ChatItemChat(Chat chatRoom) {
        super(Config.CHAT_ITEM_PATH);
        this.setProps(chatRoom);
        this.name.setText(this.getProps().getUser2().getUsername());
        this.setName(this.getProps().getUser2().getUsername());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // getter
    public Long getId() {
        return this.getProps().getUser2().getId();
    }

    // set event
    public void onClick (EventHandler<? super MouseEvent> eventHandler) {
        item.setOnMouseClicked(eventHandler);
    }
}
