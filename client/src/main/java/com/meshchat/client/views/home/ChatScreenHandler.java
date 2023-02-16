package com.meshchat.client.views.home;

import com.google.inject.Inject;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.observablebinding.ChatToChatItemChatBinding;
import com.meshchat.client.observablebinding.ScreenHandlerToNodeBinding;
import com.meshchat.client.utils.Config;
import com.meshchat.client.observablebinding.CustomUIBinding;
import com.meshchat.client.viewmodels.interfaces.IChatViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.components.ChatItem;
import com.meshchat.client.views.components.ChatItemChat;
import com.meshchat.client.views.components.ChatItemConv;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

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

        this.bindingList();

        newBtn.setOnMouseClicked((a) -> {
            if (chat.isSelected()) {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.SEARCH_CHAT_USER).show();
            }
            else {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.CREATE_CONV).show();
            }
        });
    }

    protected void bindingList() {
        // bind chatItemList -> chatItemNodeList
        chatItemList = FXCollections.observableArrayList(new ArrayList<>());
        ObservableList<Node> chatItemNodeList = this.chatList.getChildren();
        chatItemList.addListener(new ScreenHandlerToNodeBinding(chatItemNodeList));
        //
        convItemList = FXCollections.observableArrayList(new ArrayList<>());
        ObservableList<Node> convItemNodeList = this.convList.getChildren();
        convItemList.addListener(new ScreenHandlerToNodeBinding(convItemNodeList));

        viewModel.getConvList().clear();
        viewModel.getChatList().clear();
        viewModel.getChatList().addListener(new CustomUIBinding<Chat, ChatItem>(chatItemList) {
            @Override
            public ChatItem convert(Chat chatRoom) {
                ChatItemChat chatItem = new ChatItemChat(chatRoom);
                chatItem.onClick((e) -> {
                    messageScreenHandler.getViewModel().setRoomInfo(ChatRoomType.CHAT, chatRoom.id, chatRoom);
                });
                return chatItem;
            }
        });
        viewModel.getConvList().addListener(new CustomUIBinding<Conv, ChatItem>(convItemList) {
            @Override
            public ChatItem convert(Conv chatRoom) {
                ChatItemConv chatItem = new ChatItemConv(chatRoom);
                chatItem.onClick((e) -> {
                    messageScreenHandler.getViewModel().setRoomInfo(ChatRoomType.CONV, chatRoom.id, chatRoom);
                });
                return chatItem;
            }
        });
    }

    public void setMessageScreenHandler(MessageScreenHandler messageScreenHandler) {
        this.messageScreenHandler = messageScreenHandler;
    }

    @Override
    public void onShow() {
        Platform.runLater(() -> {
            this.chatList.getChildren().clear();
        });
        Platform.runLater(() -> {
            this.convList.getChildren().clear();
        });
        viewModel.fetchChatList();
        viewModel.fetchConvList();
    }
}
