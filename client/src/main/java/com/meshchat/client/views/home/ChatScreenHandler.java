package com.meshchat.client.views.home;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.Chat;
import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.ChatViewModel;
import com.meshchat.client.viewmodels.MessageViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.FXMLScreenHandler;
import com.meshchat.client.views.components.ChatItem;
import com.meshchat.client.views.components.ChatItemChat;
import com.meshchat.client.views.components.ChatItemConv;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

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
    private List<FXMLScreenHandler> chatItemList;
    private ChatViewModel viewModel;

    public ChatScreenHandler() {
        super(Config.CHAT_LIST_PATH);
    }

    @FXML
    public void initialize() {
        chatItemList = new ArrayList<>();
        viewModel = new ChatViewModel();
        viewModel.getChatMap().forEach((id, chat) -> {
            this.addChatItem(chat);
        });
//        tabPane.selectionModelProperty().addListener((o, ov, nv) -> {
//            if (nv.getSelectedItem().equals(chat)) {
//                search.setText("Find new user to chat");
//            } else {
//                search.setText("Create new conversation");
//            }
//        });
        search.setOnMouseClicked((a) -> {
            if (chat.isSelected()) {
                ModelSingleton.getInstance().stackNavigation.navigate(StackNavigation.WINDOW_LIST.SEARCH_USER).show();
            }
            else {
                ModelSingleton.getInstance().stackNavigation.navigate(StackNavigation.WINDOW_LIST.CREATE_CONV).show();
            }
        });
    }

    public void setMessageScreenHandler(MessageScreenHandler messageScreenHandler) {
        this.messageScreenHandler = messageScreenHandler;
    }

    protected void addChatItem (FXMLScreenHandler screenHandler) {
        this.chatList.getChildren().add(screenHandler.getContent());
        this.chatItemList.add(screenHandler);
    }

    public void addChatItem(Conv chatRoom) {
        ChatItem chatItem = new ChatItemConv(chatRoom);
        // TODO: implement this event
        chatItem.onClick((e) -> {
            System.out.println("Clicked");
            messageScreenHandler.getViewModel().setRoomInfo(MessageViewModel.Type.CONV, chatRoom.id);
        });
        addChatItem(chatItem);
    }

    public void addChatItem(Chat chatRoom) {
        ChatItem chatItem = new ChatItemChat(chatRoom);
        // TODO: implement this event
        chatItem.onClick((e) -> {
            messageScreenHandler.getViewModel().setRoomInfo(MessageViewModel.Type.CHAT, chatRoom.id);
        });
        addChatItem(chatItem);
    }

//
//    public void removeChatItem(Long room_id, MessageViewModel.Type type) {
//        for (int i = 0; i < chatItemList.size(); i++) {
//            ChatItem item = (ChatItem) chatItemList.get(i);
//            if (((ChatItem) item).getId().equals(room_id)  && ((ChatItem) item)() == type) {
//                this.chatItemList.remove(i);
//                this.chatList.getChildren().remove(i);
//                break;
//            }
//        }
//    }

    @FXML
    public void onSearchNewUser() {
        System.out.println("abc");
    }

}
