package com.meshchat.client.views.components;

import com.meshchat.client.model.Message;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class MsgItemLeft extends MsgItem {
    @FXML
    private Text content;

    public MsgItemLeft(Message message) {
        super(Config.MSG_SEND_ITEM_PATH, message);
        this.content.setText(message.getEntity().getContent());
    }
}
