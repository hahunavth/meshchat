package com.meshchat.client.views.home;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Message;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.MessageViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.components.MsgItem;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.factories.MsgItemComponentFactory;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageScreenHandler extends BaseScreenHandler implements LazyInitialize {

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

    @FXML
    private Button submitBtn;
    @FXML
    private Button infoBtn;

    private MessageViewModel viewModel;
    private MsgItemComponentFactory msgItemComponentFactory;
    private List<MsgItem> msgItemList = new ArrayList<>();

    public MessageScreenHandler() {
        super(Config.MSG_FLOW_PATH);
    }

    @FXML
    public void initialize () {
        // if add msg -> scroll to bottom
        this.msgItemComponentFactory = new MsgItemComponentFactory();
        this.viewModel = new MessageViewModel();
        msgList.heightProperty().addListener(observable -> scroll.setVvalue(1D));
        this.username.textProperty().bindBidirectional(this.viewModel.getName());
        // on change room update all
        this.viewModel.getRoomId().addListener((observable, oldValue, newValue) -> {
            this.onShow();
        });
        //
        this.viewModel.getMsgList().forEach((item) -> {
            MsgItem msgItem = msgItemComponentFactory.getItem(item, ModelSingleton.getInstance().dataStore.getUserProfile().getEntity().getId());
            addMsg(msgItem);
        });
        this.viewModel.getMsgList().addListener((ListChangeListener<? super Message>) e -> {
            while (e.next()) {
                if (e.wasAdded()) {
                    System.out.println("add");
                    List<Message> ins = (List<Message>) e.getAddedSubList();
                    ins.forEach(item -> {
                        MsgItem msgItem = msgItemComponentFactory.getItem(item, ModelSingleton.getInstance().dataStore.getUserProfile().getEntity().getId());
                        addMsg(msgItem);
                    });
                }
                if (e.wasRemoved()) {
                    // on remove -> remove all
                    System.out.println("remove all");
                    this.msgItemList.removeAll(this.msgItemList);
                    this.msgList.getChildren().forEach(i -> {
                        Platform.runLater(() -> {
                            this.msgList.getChildren().removeAll(this.msgList.getChildren());
//                            this.msgList.getChildren().remove(i);
                        });
                    });
//                    e.next();
                }
                if (e.wasUpdated()) {
                    // handle msg was deleted
                }
                if (e.wasReplaced()) {

                }
                if (e.wasPermutated()) {

                }
            }
        });

        // fixme: not working
        infoBtn.setOnMouseClicked(a -> {
            if (this.viewModel.getType() == ChatRoomType.CHAT)
                ModelSingleton.getInstance().stackNavigation.navigate(StackNavigation.WINDOW_LIST.USER_INFO);
            else if (this.viewModel.getType() == ChatRoomType.CONV)
                ModelSingleton.getInstance().stackNavigation.navigate(StackNavigation.WINDOW_LIST.CONV_INFO);
        });

        this.viewModel.setRoomInfoHandler((e) -> {
                    try {
                        this.viewModel.fetchMsgList();
                    } catch (APICallException ex) {
                        DialogScreenHandler screenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
                        screenHandler.getViewModel().setMessage(ex.getMessage());
                        screenHandler.show();
                    }
                });

        this.submitBtn.setOnMouseClicked(this::onSubmit);
        this.input.setOnAction(this::onSubmit);
    }

    public void onSubmit(Event event) {
        try {
            System.out.println("onSubmit");
            this.viewModel.sendMsg(input.getText());
            this.input.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessageViewModel getViewModel() {
        return viewModel;
    }

    protected void addMsg(MsgItem item) {
        this.msgItemList.add(item);
        Platform.runLater(() -> {
            this.msgList.getChildren().add(item.getContent());
        });
    }


//    public void addMsg (String content, Long from_uid, Long uid, Long msg_id) {
//         TODO: handle msg id
//        this.addMsg(new MsgItem(content, Objects.equals(from_uid, uid)));
//    }

    public void disableMsg (Long msg_id) {
        // TODO: implement
        System.out.println("Todo");
    }

    public String getText() {
        return this.input.getText();
    }

    public void setName (String name) {
        this.username.setText(name);
    }

    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void onShow() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
            // todo: dialog
        }
    }
}
