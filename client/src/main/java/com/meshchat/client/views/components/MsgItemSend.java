package com.meshchat.client.views.components;

import com.meshchat.client.model.Message;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.utils.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class MsgItemSend extends MsgItem {


    @FXML
    private Text content;

    public MsgItemSend(Message message) {
        super(Config.MSG_SEND_ITEM_PATH, message);
        this.content.setText(message.getEntity().getContent());
    }
}
