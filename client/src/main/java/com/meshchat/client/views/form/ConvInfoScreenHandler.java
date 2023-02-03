package com.meshchat.client.views.form;

import com.google.inject.Inject;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.navigation.StackNavigation;

public class ConvInfoScreenHandler extends BaseScreenHandler {
    @Inject
    public ConvInfoScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation) {
        super(Config.CONV_INFO_PATH, navigation);
    }

    @Override
    public void onShow() {

    }
}
