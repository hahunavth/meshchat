package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.settings.SettingDetailsScreenHandler;

public class SettingScreenFactory extends ScreenFactory implements LazyInitialize {
    @Override
    public BaseController getController() {
        return null;
    }
    @Override
    public SettingDetailsScreenHandler getScreenHandler() {
        return new SettingDetailsScreenHandler(this.stage);
    }
}
