package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.ChatController;
import com.meshchat.client.views.home.ChatScreenHandler;

public class ChatScreenFactory extends ScreenFactory {
    @Override
    public ChatController getController() {
        return new ChatController();
    }

    @Override
    public ChatScreenHandler getScreenHandler() {
        ChatScreenHandler chatScreenHandler = new ChatScreenHandler();
        return chatScreenHandler;
    }
}
