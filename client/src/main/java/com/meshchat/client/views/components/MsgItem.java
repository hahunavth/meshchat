package com.meshchat.client.views.components;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MsgItem extends BaseComponent {
    @FXML
    public Text content;

    public MsgItem(String text) {
        super(Config.MSG_SEND_ITEM_PATH);
        this.content.setText(text);
    }

    public MsgItem(String text, boolean isReceved) {
        super(Config.MSG_RECV_ITEM_PATH);
        this.content.setText(text);
    }
}
