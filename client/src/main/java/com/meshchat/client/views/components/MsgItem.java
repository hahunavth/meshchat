package com.meshchat.client.views.components;

import com.meshchat.client.model.Message;
import com.meshchat.client.views.base.BaseComponent;

public abstract class MsgItem extends BaseComponent<Message> {
    public MsgItem(String screenPath, Message message) {
        super(screenPath);
        this.setProps(message);
    }

    public Long getId() {
        return this.getProps().getEntity().getId();
    }
}
