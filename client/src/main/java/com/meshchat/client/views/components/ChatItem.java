package com.meshchat.client.views.components;

import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public abstract class ChatItem<Props> extends BaseComponent<Props> implements Initializable {
    public ChatItem(String screenPath) {
        super(screenPath);
    }

    public abstract Long getId();
    public abstract void onClick (EventHandler<? super MouseEvent> eventHandler);
}
