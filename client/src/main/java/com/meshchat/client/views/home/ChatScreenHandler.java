package com.meshchat.client.views.home;

import com.google.inject.Inject;
import com.meshchat.client.Launcher;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.utils.Config;
import com.meshchat.client.utils.CustomUIBinding;
import com.meshchat.client.viewmodels.ChatViewModel;
import com.meshchat.client.viewmodels.interfaces.IChatViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.FXMLScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.components.ChatItem;
import com.meshchat.client.views.components.ChatItemChat;
import com.meshchat.client.views.components.ChatItemConv;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ChatScreenHandler:
 * Danh sách các chat.
 */
public class ChatScreenHandler extends BaseScreenHandler {
    @FXML
    private VBox chatList;
    @FXML
    private VBox convList;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab chat;
    @FXML
    private Tab conv;

    @FXML
    private Button newBtn;

    private MessageScreenHandler messageScreenHandler;
    private ObservableList<ChatItem> chatItemList;
    private ObservableList<ChatItem> convItemList;
    private IChatViewModel viewModel;

    @Inject
    public ChatScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, IChatViewModel viewModel) {
        super(Config.CHAT_LIST_PATH, navigation);

        this.viewModel = viewModel;

        // bind chatItemList -> chatItemNodeList
        chatItemList = FXCollections.observableArrayList(new ArrayList<>());
        ObservableList<Node> chatItemNodeList = this.chatList.getChildren();
        chatItemList.addListener(new CustomUIBinding<FXMLScreenHandler, Node>(chatItemNodeList) {
            @Override
            public Node convert(FXMLScreenHandler fxmlScreenHandler) {
                return fxmlScreenHandler.getContent();
            }
        });
        //
        convItemList = FXCollections.observableArrayList(new ArrayList<>());
        ObservableList<Node> convItemNodeList = this.convList.getChildren();
        convItemList.addListener(new CustomUIBinding<FXMLScreenHandler, Node>(convItemNodeList) {
            @Override
            public Node convert(FXMLScreenHandler fxmlScreenHandler) {
                return fxmlScreenHandler.getContent();
            }
        });

        viewModel = Launcher.injector.getInstance(ChatViewModel.class);
        // init chat
        viewModel.getChatMap().forEach((id, chat) -> {
            this.addChatItem(chat);
        });
        // event add chat
        viewModel.getChatMap().addListener((MapChangeListener<? super Long, ? super Chat>) (e) -> {
            Long key = e.getKey();
            if (e.wasAdded()) {
                Chat chat = e.getValueAdded();
                chat.id = key;
                this.addChatItem(chat);
            } else if (e.wasRemoved()) {
                this.viewModel.getChatMap().remove(key);
            }
        });
        viewModel.getConvMap().forEach((id, conv) -> {
            this.addChatItem(conv);
        });
        viewModel.getConvMap().addListener((MapChangeListener<? super Long, ? super Conv>) (e) -> {
            Long key = e.getKey();
            if (e.wasAdded()) {
                Conv conv = e.getValueAdded();
                conv.id = key;
                this.addChatItem(conv);
            } else if (e.wasRemoved()) {
                this.viewModel.getConvMap().remove(key);
            }
        });

        newBtn.setOnMouseClicked((a) -> {
            if (chat.isSelected()) {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.SEARCH_CHAT_USER).show();
            }
            else {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.CREATE_CONV).show();
            }
        });

        IChatViewModel finalViewModel = viewModel;
        tabPane.setOnMouseClicked(e -> {
//            Platform.runLater(() -> {
//                this.chatList.getChildren().clear();
//            });
//            Platform.runLater(() -> {
//                this.convList.getChildren().clear();
//            });
            finalViewModel.fetchChatList();
            finalViewModel.fetchConvList();
        });
    }

    public void setMessageScreenHandler(MessageScreenHandler messageScreenHandler) {
        this.messageScreenHandler = messageScreenHandler;
    }

    protected void addChatItem (ChatItemChat screenHandler) {
        this.chatItemList.add(screenHandler);
    }

    protected void addChatItem (ChatItemConv screenHandler) {
        this.convItemList.add(screenHandler);
    }

    public void addChatItem(Conv chatRoom) {
        ChatItemConv chatItem = new ChatItemConv(chatRoom);
        // TODO: implement this event
        chatItem.onClick((e) -> {
            System.out.println("Clicked");
            messageScreenHandler.getViewModel().setRoomInfo(ChatRoomType.CONV, chatRoom.id, chatRoom);
        });
        addChatItem(chatItem);
    }

    public void addChatItem(Chat chatRoom) {
        ChatItemChat chatItem = new ChatItemChat(chatRoom);
        chatItem.onClick((e) -> {
            messageScreenHandler.getViewModel().setRoomInfo(ChatRoomType.CHAT, chatRoom.id, chatRoom);
        });
        addChatItem(chatItem);
    }

    @Override
    public void onShow() {
//        System.out.println("showwwww");
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
                // wait 1 second
//                Thread.sleep(1000);
//                System.out.println("call api");
        Platform.runLater(() -> {
            this.chatList.getChildren().clear();
        });
        Platform.runLater(() -> {
            this.convList.getChildren().clear();
        });
        viewModel.fetchChatList();
        viewModel.fetchConvList();
//                return null;
//            }
//        };
        // fetch data
//        new Thread(task).start();
    }
}
