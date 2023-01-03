package com.meshchat.client.views.home;

import com.meshchat.client.Launcher;
import com.meshchat.client.ModelSingleton;
import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.controllers.MessageController;
import com.meshchat.client.model.Message;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.components.MsgItem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MessageScreenHandler extends BaseScreenHandler<MessageController> implements LazyInitialize {

    @FXML
    private VBox msgList;
    @FXML
    private ImageView send;
    @FXML
    private TextField input;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Text username;

    public MessageScreenHandler(Stage stage) {
        super(Config.MSG_FLOW_PATH);
    }

    @FXML
    public void initialize () {
        // if add msg -> scroll to bottom
        msgList.heightProperty().addListener(observable -> scroll.setVvalue(1D));
    }

    protected void addMsg(MsgItem item) {
        this.msgList.getChildren().add(item.getContent());
    }

    public void addMsg (String content, Long from_uid, Long uid, Long msg_id) {
        // TODO: handle msg id
        this.addMsg(new MsgItem(content, Objects.equals(from_uid, uid)));
    }

    public void disableMsg (Long msg_id) {
        // TODO: implement
        System.out.println("Todo");
    }

    public void setOnSubmit (EventHandler<ActionEvent> e) {
        this.input.setOnAction(e);
    }

    public String getText() {
        return this.input.getText();
    }
//    @FXML
//    public void submit() {
//        addMsg(new MsgItem(this.input.getText()));
////        this.controller.sendMsg(this.input.getText());
//        ModelSingleton.getInstance().tcpClient.send(this.input.getText());
//        this.input.setText("");
//    }

    @Override
    public void setBaseController(MessageController controller) {
        super.setBaseController( (MessageController) controller);

//        if (this.getBaseController().getType() == MessageController.Type.CHAT) {
//
//            this.username.setText(this.getBaseController().getUserName());
//
//            long uid = this.getBaseController().getUserId();
//            List<Message> msgList = this.getBaseController().getMsgList();
//            if (msgList != null) {
//                for (Message msg : msgList) {
//                    addMsg(new MsgItem(msg.content, msg.from_user_id != uid));
//                }
//            }
//        }
    }

    public void setName (String name) {
        this.username.setText(name);
    }


    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;
    }
}
