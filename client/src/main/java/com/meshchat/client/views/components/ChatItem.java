package com.meshchat.client.views.components;

import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public abstract class ChatItem<Props> extends BaseComponent<Props> implements Initializable {
    @FXML
    private Button avatar;

    public ChatItem(String screenPath) {
        super(screenPath);
    }

    public abstract Long getId();
    public abstract void onClick (EventHandler<? super MouseEvent> eventHandler);
    public void setName (String name) {
        this.avatar.setText(name);
    }
}
