package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.controllers.MessageController;
import com.meshchat.client.views.home.MessageScreenHandler;

public class MessageScreenFactory extends ScreenFactory {

    @Override
    public MessageController getController() {
        return new MessageController();
    }

    @Override
    public MessageScreenHandler getScreenHandler() {
        return new MessageScreenHandler(this.stage);
    }
}
