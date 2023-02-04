package com.meshchat.client.views.home;

import com.google.inject.Inject;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Message;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.utils.Config;
import com.meshchat.client.utils.CustomUIBinding;
import com.meshchat.client.viewmodels.interfaces.IMessageViewModel;
import com.meshchat.client.viewmodels.interfaces.IPaginateViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.components.MsgItem;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.factories.MsgItemComponentFactory;
import com.meshchat.client.views.form.UserProfileScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageScreenHandler extends BaseScreenHandler implements LazyInitialize {

    @FXML
    private Button avatar;

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

    @FXML
    private Button nextBtn;
    @FXML
    private Button prevBtn;


    private final IMessageViewModel viewModel;
    private IPaginateViewModel paginateViewModel;
    private final MsgItemComponentFactory msgItemComponentFactory;
    private final ObservableList<MsgItem> msgItemList;

    @Inject
    public MessageScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, IMessageViewModel viewModel, IPaginateViewModel paginateViewModel) {
        super(Config.MSG_FLOW_PATH, navigation);

        // if add msg -> scroll to bottom
        msgList.heightProperty().addListener(observable -> scroll.setVvalue(1D));

        // set vm
        this.viewModel = viewModel;
        this.paginateViewModel = paginateViewModel;

        // binding
        this.msgItemComponentFactory = new MsgItemComponentFactory();
        this.username.textProperty().bindBidirectional(this.viewModel.getName());
        this.avatar.textProperty().bindBidirectional(this.viewModel.getName());
        // bind msgItemList -> msgItemListNode
        List<MsgItem> _msgItemList = new ArrayList<>();
        msgItemList = FXCollections.observableArrayList(_msgItemList);
        ObservableList<Node> msgNodeList = this.msgList.getChildren();
        msgItemList.addListener(new CustomUIBinding<MsgItem, Node>(msgNodeList) {
            @Override
            public Node convert(MsgItem msgItem) {
                return msgItem.getContent();
            }
        });

//        this.viewModel.getRoomId().addListener((observable, oldValue, newValue) -> {
//            this.onShow();  // on change room update all
//        });
        // add msg if existed
        this.viewModel.getMsgList().forEach((item) -> {
            MsgItem msgItem = msgItemComponentFactory.getItem(item, this.viewModel.getCurrentUserId());
            addMsg(msgItem);
        });
        // event
        this.viewModel.getMsgList().addListener(this::onMsgListChange);
        this.infoBtn.setOnAction(this::onInfoBtnPressed);
        this.viewModel.setRoomInfoHandler(this::handleFetchRoomInfo);
        this.submitBtn.setOnAction(this::onSubmit);
        this.input.setOnAction(this::onSubmit);
        this.nextBtn.setOnMouseClicked((event -> {
            try {
                this.paginateViewModel.goToNextPage();
                this.refreshMsgList();
            } catch (Exception e) {
                this.nextBtn.setVisible(false);
            }
            this.prevBtn.setVisible(true);
        }));
        this.prevBtn.setOnMouseClicked(event -> {
            this.paginateViewModel.goToPrevPage();
            try {
                this.refreshMsgList();
            } catch (APICallException e) {
                    this.prevBtn.setVisible(false);
            }
            this.nextBtn.setVisible(true);
        });
    }

    public void onMsgListChange(ListChangeListener.Change<? extends Message> e) {
        while (e.next()) {
            if (e.wasAdded()) {
                List<Message> ins = (List<Message>) e.getAddedSubList();
                ins.forEach(item -> {
                    MsgItem msgItem = msgItemComponentFactory.getItem(item, this.viewModel.getCurrentUserId());
                    addMsg(msgItem);
                });
            }
            if (e.wasRemoved()) {
                // on remove -> remove all
                this.msgItemList.removeAll(this.msgItemList);
                this.msgList.getChildren().forEach(i -> {
                    Platform.runLater(() -> {
                        this.msgList.getChildren().removeAll(this.msgList.getChildren());
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
    }

    public void onInfoBtnPressed (Event event) {
        System.out.println("Info btn pressed");
        if (this.viewModel.getType() == ChatRoomType.CHAT) {
            UserProfileScreenHandler userProfileScreenHandler = (UserProfileScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.USER_INFO);
            userProfileScreenHandler.getViewModel().setUserId(this.viewModel.getRoomId().get());
            userProfileScreenHandler.show();
        }
        else if (this.viewModel.getType() == ChatRoomType.CONV)
            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.CONV_INFO).show();
    }

    public void handleFetchRoomInfo(Event e) {
        try {
            this.paginateViewModel.resetPage();
            this.viewModel.fetchMsgList(this.paginateViewModel.getPageSize(), this.paginateViewModel.getOffset());
        } catch (APICallException ex) {
            DialogScreenHandler screenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
            screenHandler.getViewModel().setMessage(ex.getMessage());
            screenHandler.show();
        }
    }

    public void refreshMsgList() throws APICallException {
        this.viewModel.fetchMsgList(this.paginateViewModel.getPageSize(), this.paginateViewModel.getOffset());
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

    public IMessageViewModel getViewModel() {
        return viewModel;
    }

    protected void addMsg(MsgItem item) {
        this.msgItemList.add(item);
    }

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


    private ScheduledExecutorService executor;

    @Override
    public void onShow() {
        System.out.println("showwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            // NOTE: HARDCODE LIMIT AND OFFSET HERE
            try {
                this.paginateViewModel.resetPage();
                this.viewModel.notifyMsgList(this.paginateViewModel.getPageSize(), this.paginateViewModel.getOffset());
            } catch (APICallException e) {
                throw new RuntimeException(e);
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void show() {
        super.show();
        this.onShow();
    }
}
