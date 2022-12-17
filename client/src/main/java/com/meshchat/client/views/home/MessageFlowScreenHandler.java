package com.meshchat.client.views.home;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MessageFlowScreenHandler extends BaseScreenHandler {

    @FXML
    private VBox msgList;

    @FXML
    private ImageView send;

    @FXML
    private TextField input;

    @FXML
    private ScrollPane scroll;

    public MessageFlowScreenHandler(Stage stage) throws IOException {
        super(stage, Config.MSG_FLOW_PATH);
        addMsg(new MsgItem(stage, "Helloooooo"));
        addMsg(new MsgItem(stage, "Hi hi", true));
        // if add msg -> scroll to bottom
        msgList.heightProperty().addListener(observable -> scroll.setVvalue(1D));
    }

    public void addMsg(MsgItem item) {
        this.msgList.getChildren().add(item.getContent());
    }

    @FXML
    public void submit() throws IOException {
        addMsg(new MsgItem(stage, this.input.getText()));
        this.input.setText("");
    }
}
