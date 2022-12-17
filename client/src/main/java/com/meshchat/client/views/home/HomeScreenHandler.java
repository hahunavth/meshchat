package com.meshchat.client.views.home;

import com.meshchat.client.views.layout.HomeLayout;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeScreenHandler extends HomeLayout {
    public HomeScreenHandler(Stage stage) throws IOException {
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
