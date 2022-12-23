package com.meshchat.client.views.home;

import com.meshchat.client.Launcher;
import com.meshchat.client.controllers.MessageListController;
import com.meshchat.client.views.layout.HomeLayout;
import javafx.stage.Stage;
import javafx.application.Platform;


import java.io.IOException;

public class HomeScreenHandler extends HomeLayout {
    public HomeScreenHandler(Stage stage) {
        super(stage);
        this.stage.setTitle("Home");

        // new screen session
        ChatScreenHandler chat = new ChatScreenHandler(stage);
        MessageFlowScreenHandler msg = new MessageFlowScreenHandler(stage);
        // set routing
        chat.setPreviousScreen(this);
        msg.setPreviousScreen(chat);
        // add to screen
        addSessionContent(HomeLayout.SIDEBAR, chat);
        addSessionContent(HomeLayout.CONTENT, msg);
    }

}
