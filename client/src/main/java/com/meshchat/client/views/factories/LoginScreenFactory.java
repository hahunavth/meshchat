package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.views.login.LoginScreenHandler;
import javafx.stage.Stage;

public class LoginScreenFactory extends ScreenFactory {
    LoginScreenHandler screenHandler;
    @Override
    public BaseController getController() {
        return null;
    }

    @Override
    public LoginScreenHandler getScreenHandler() {
        screenHandler = new LoginScreenHandler();
        if (this.stage != null) {
            screenHandler.lazyInitialize(this.stage);
        }
        return screenHandler;
    }

    @Override
    public void lazyInitialize(Stage stage) {
        super.lazyInitialize(stage);
        if (this.screenHandler != null) {
            screenHandler.lazyInitialize(stage);
        }
    }
}
