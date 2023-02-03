package com.meshchat.client.views.settings;

import com.google.inject.Inject;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingDetailsScreenHandler extends BaseScreenHandler implements LazyInitialize {

    @Inject
    public SettingDetailsScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation) {
        super(Config.SETTING_DETAILS_PATH, navigation);
    }

    public void onShow() {

    }
}
