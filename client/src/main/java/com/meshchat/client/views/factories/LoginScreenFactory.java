package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.views.login.LoginScreenHandler;

public class LoginScreenFactory extends ScreenFactory {
    @Override
    public BaseController getController() {
        return null;
    }

    @Override
    public LoginScreenHandler getScreenHandler() {
        return new LoginScreenHandler(this.stage);
    }
}
