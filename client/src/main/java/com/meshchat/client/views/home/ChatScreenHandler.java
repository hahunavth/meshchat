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
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ChatScreenHandler:
 * Danh sách các chat.
 */
public class ChatScreenHandler extends BaseScreenHandler {
    @FXML
    private VBox chatList;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab chat;
    @FXML
    private Tab conv;

    @FXML
    private TextField search;

    private MessageScreenHandler messageScreenHandler;
    private ObservableList<FXMLScreenHandler> chatItemList;
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
                System.out.println(chat.id);
                this.addChatItem(chat);
            } else if (e.wasRemoved()) {
                // TODO: IMPL
//                this.removeChatItem(key, MessageViewModel.Type.CHAT);
            }
        });

        search.setOnMouseClicked((a) -> {
            if (chat.isSelected()) {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.SEARCH_USER).show();
            }
            else {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.CREATE_CONV).show();
            }
        });
    }

    public void setMessageScreenHandler(MessageScreenHandler messageScreenHandler) {
        this.messageScreenHandler = messageScreenHandler;
    }

    protected void addChatItem (FXMLScreenHandler screenHandler) {
        this.chatItemList.add(screenHandler);
    }

    public void addChatItem(Conv chatRoom) {
        ChatItem chatItem = new ChatItemConv(chatRoom);
        // TODO: implement this event
        chatItem.onClick((e) -> {
            System.out.println("Clicked");
            messageScreenHandler.getViewModel().setRoomInfo(ChatRoomType.CONV, chatRoom.id, chatRoom);
        });
        addChatItem(chatItem);
    }

    public void addChatItem(Chat chatRoom) {
        ChatItem chatItem = new ChatItemChat(chatRoom);
        // TODO: implement this event
        chatItem.onClick((e) -> {
            messageScreenHandler.getViewModel().setRoomInfo(ChatRoomType.CHAT, chatRoom.id, chatRoom);
        });
        addChatItem(chatItem);
    }

    @FXML
    public void onSearchNewUser() {
        System.out.println("abc");
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
                viewModel.fetchChatList();
                viewModel.fetchConvList();
//                return null;
//            }
//        };
        // fetch data
//        new Thread(task).start();
    }
}
