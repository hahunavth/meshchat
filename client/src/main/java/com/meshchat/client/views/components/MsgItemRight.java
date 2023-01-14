package com.meshchat.client.views.components;

import com.meshchat.client.model.Message;
import com.meshchat.client.utils.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class MsgItemRight extends MsgItem {

    @FXML
    private HBox all;
    @FXML
    private Button contextBtn;

    @FXML
    private MenuItem del;

    @FXML
    private Text content;
    public MsgItemRight(Message message) {
        super(Config.MSG_RECV_ITEM_PATH, message);
        this.content.setText(message.getEntity().getContent());
    }
}
