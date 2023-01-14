package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.home.ChatScreenHandler;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.home.MessageScreenHandler;
import com.meshchat.client.views.layout.HomeLayout;

public class HomeScreenFactory implements ScreenFactory<HomeScreenHandler> {

    @Override
    public HomeScreenHandler getScreenHandler() {
        HomeScreenHandler home = new HomeScreenHandler();
        ChatScreenHandler chat = new ChatScreenHandler();
        MessageScreenHandler message = new MessageScreenHandler();
        chat.setMessageScreenHandler(message);

        // add to screen
        home.addSessionContent(HomeLayout.Sessions.SIDEBAR, chat);
        home.addSessionContent(HomeLayout.Sessions.CONTENT, message);

        return home;
    }
}
