package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseLayout;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.layout.HomeLayout;
import com.meshchat.client.views.layout.TabsLayout;
import com.meshchat.client.views.navigation.TabNavigation;
import com.meshchat.client.views.settings.SettingDetailsScreenHandler;
import javafx.stage.Stage;

public class HomeWindowFactory extends ScreenFactory {

    BaseLayout layout;
    TabNavigation navigation;
    HomeScreenHandler home;
    SettingDetailsScreenHandler setting;
    boolean isCreated = false;

    @Override
    public BaseController getController() {
        return null;
    }

    @Override
    public BaseLayout getScreenHandler() {
        layout = new TabsLayout();

        navigation = new TabNavigation(layout);

        layout.addSessionContent(TabsLayout.Sessions.TAB, navigation);

        HomeScreenFactory homeScreenFactory = new HomeScreenFactory();
        SettingScreenFactory settingScreenFactory = new SettingScreenFactory();

        home = homeScreenFactory.getScreenHandler();
        setting = settingScreenFactory.getScreenHandler();

        navigation.addScreenHandler(Config.MSG_ICON_PATH, home);
        navigation.addScreenHandler(Config.SETTING_ICON_PATH, setting);

        isCreated = true;

        if (stage != null) {
            this.layout.lazyInitialize(stage);
            this.home.lazyInitialize(stage);
            this.setting.lazyInitialize(stage);
        }

        return layout;
    }

    @Override
    public void lazyInitialize(Stage stage) {
        super.lazyInitialize(stage);
        if (isCreated) {
            this.layout.lazyInitialize(stage);
            this.home.lazyInitialize(stage);
            this.setting.lazyInitialize(stage);
        }
    }
}
