package com.meshchat.client.views.home;

import com.meshchat.client.Launcher;
import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.controllers.MessageListController;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    
    private MessageListController controller;

    public MessageFlowScreenHandler(Stage stage) {
        super(stage, Config.MSG_FLOW_PATH);
        // if add msg -> scroll to bottom
        msgList.heightProperty().addListener(observable -> scroll.setVvalue(1D));

        Launcher.tcpClient.setMsgHandler((e) -> {
            String msg = (String) e.getSource();
            // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
            Platform.runLater(
                    () -> {
                        // Update UI here.
                        addMsg(new MsgItem(stage, msg, true));
                    }
            );

        });
    }

    public void addMsg(MsgItem item) {
        this.msgList.getChildren().add(item.getContent());
    }

    @FXML
    public void submit() throws IOException {
        addMsg(new MsgItem(stage, this.input.getText()));
//        this.controller.sendMsg(this.input.getText());
        Launcher.tcpClient.send(this.input.getText());
        this.input.setText("");
    }

    @Override
    public MessageListController getBaseController() {
        return this.controller;
    }
    @Override
    public void setBaseController(BaseController controller) {
        super.setBaseController(controller);
    }
}
