package com.meshchat.client.views.settings;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.LazyInitialize;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingDetailsScreenHandler extends BaseScreenHandler implements LazyInitialize {

    public SettingDetailsScreenHandler() {
        super(Config.SETTING_DETAILS_PATH);
    }

    public void onShow() {

    }
}
