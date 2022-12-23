package com.meshchat.client.views.home;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MsgItem extends BaseScreenHandler {
    @FXML
    public Text content;

    public MsgItem(Stage stage, String text) {
        super(stage, Config.MSG_SEND_ITEM_PATH);
        this.content.setText(text);
    }

    public MsgItem(Stage stage, String text, boolean isReceved) {
        super(stage, Config.MSG_RECV_ITEM_PATH);
        this.content.setText(text);
    }
}
