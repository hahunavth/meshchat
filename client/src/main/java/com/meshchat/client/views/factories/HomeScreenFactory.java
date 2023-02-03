package com.meshchat.client.views.factories;

import com.meshchat.client.Launcher;
import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.home.ChatScreenHandler;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.home.MessageScreenHandler;
import com.meshchat.client.views.layout.HomeLayout;

public class HomeScreenFactory implements ScreenFactory<HomeScreenHandler> {

    @Override
    public HomeScreenHandler getScreenHandler() {
        HomeScreenHandler home = Launcher.injector.getInstance(HomeScreenHandler.class);
        ChatScreenHandler chat = Launcher.injector.getInstance(ChatScreenHandler.class);
        MessageScreenHandler message = Launcher.injector.getInstance(MessageScreenHandler.class);

        chat.setMessageScreenHandler(message);

        // add to screen
        home.addSessionContent(HomeLayout.Sessions.SIDEBAR, chat);
        home.addSessionContent(HomeLayout.Sessions.CONTENT, message);

        return home;
    }
}
