package com.meshchat.client.views.factories;

import com.meshchat.client.Launcher;
import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.login.LoginScreenHandler;

/**
 * Login window
 */
public class LoginWindowFactory implements ScreenFactory<LoginScreenHandler> {
    @Override
    public LoginScreenHandler getScreenHandler() {
        return Launcher.injector.getInstance(LoginScreenHandler.class);
    }
}
