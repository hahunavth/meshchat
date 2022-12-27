package com.meshchat.client.views.components;

import com.meshchat.client.controllers.MessageController;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatItem extends BaseComponent {

    @FXML
    private Text name;
    @FXML
    private Text lastmsg;
    @FXML
    private HBox item;

    private Long id;
    private MessageController.Type type;

    public ChatItem() {
        super(Config.CHAT_ITEM_PATH);
    }

    @FXML
    public void initialize() {
    }

    public void setOnClick (EventHandler<? super MouseEvent> eventHandler) {
        item.setOnMouseClicked(eventHandler);
    }

    public void setName (String name) {
        this.name.setText(name);
    }

    public void setLastmsg (String lastMsg) {
        this.lastmsg.setText(lastMsg);
    }

    public String getName () {
        return this.name.getText();
    }

    public String getLastMsg () {
        return this.lastmsg.getText();
    }

    public MessageController.Type getType() {
        return type;
    }

    public void setType(MessageController.Type type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
