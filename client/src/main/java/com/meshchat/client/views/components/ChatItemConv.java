package com.meshchat.client.views.components;

import com.meshchat.client.model.ChatGen;
import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.MessageViewModel;
import com.meshchat.client.views.base.BaseComponent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatItemConv extends ChatItem<Conv> {

    @FXML
    private Text name;
    @FXML
    private Text lastmsg;
    @FXML
    private HBox item;

    public ChatItemConv(Conv chatRoom) {
        super(Config.CHAT_ITEM_PATH);
        this.setProps(chatRoom);
        this.name.setText(this.getProps().getName().get());
        this.setName(this.getProps().getName().get());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // getter
    @Override
    public Long getId() {
        return this.getProps().id;
    }

    // set event
    @Override
    public void onClick (EventHandler<? super MouseEvent> eventHandler) {
        item.setOnMouseClicked(eventHandler);
    }
}
