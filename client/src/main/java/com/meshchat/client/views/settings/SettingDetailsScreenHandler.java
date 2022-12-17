package com.meshchat.client.views.settings;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingDetailsScreenHandler extends BaseScreenHandler {

    public SettingDetailsScreenHandler(Stage stage) throws IOException {
        super(stage, Config.SETTING_DETAILS_PATH);
    }
}
