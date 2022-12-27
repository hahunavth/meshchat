package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.controllers.MessageController;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.home.ChatScreenHandler;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.home.MessageScreenHandler;
import com.meshchat.client.views.layout.HomeLayout;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenFactory extends ScreenFactory implements LazyInitialize {

    List<LazyInitialize> lazyList = new ArrayList<>();

    @Override
    public BaseController getController() {
        return null;
    }

    @Override
    public HomeScreenHandler getScreenHandler() {
        HomeScreenHandler home = new HomeScreenHandler();
        // new screen session
        ChatScreenFactory chat = new ChatScreenFactory();
        MessageScreenFactory msg = new MessageScreenFactory();
        //
        ChatScreenHandler chatScreenHandler = chat.getScreenHandler();
        MessageScreenHandler messageScreenHandler = msg.getScreenHandler();
        // set routing
        chatScreenHandler.setPreviousScreen(home);
        messageScreenHandler.setPreviousScreen(chatScreenHandler);
        //
        // add controller
        chatScreenHandler.setBaseController(chat.getController());
        MessageController messageController = msg.getController();
//        messageController.setRoomInfo(MessageController.Type.CHAT, -1L);
        messageScreenHandler.setBaseController(messageController);
//        //
        chatScreenHandler.setMessageScreenHandler(messageScreenHandler, msg);
//        // add to screen
        home.addSessionContent(HomeLayout.Sessions.SIDEBAR, chatScreenHandler);
        home.addSessionContent(HomeLayout.Sessions.CONTENT, messageScreenHandler);

        lazyList.add(home);
        lazyList.add(messageScreenHandler);

        return home;
    }

    @Override
    public void lazyInitialize(Stage stage) {
        for (LazyInitialize lazyInitialize : this.lazyList) {
            lazyInitialize.lazyInitialize(stage);
        }
    }

}
