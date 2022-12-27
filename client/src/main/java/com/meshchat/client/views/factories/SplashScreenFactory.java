package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.views.base.BaseScreenHandler;

public class SplashScreenFactory extends ScreenFactory {
    public SplashScreenFactory() {
        super();
    }

    @Override
    public BaseController getController() {
        return null;
    }

    @Override
    public BaseScreenHandler getScreenHandler() {
//        return new SplashScreenHandler(this.stage);
        return null;
    }
}
