package com.meshchat.client.launchers;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.layout.TabsLayout;
import com.meshchat.client.views.navigation.TabNavigation;
import com.meshchat.client.views.settings.SettingDetailsScreenHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class TabNavigationPreview extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        TabsLayout layout = new TabsLayout();
        layout.lazyInitialize(stage);
        TabNavigation nav = new TabNavigation(layout);
        layout.addSessionContent(TabsLayout.Sessions.TAB, nav);
        com.meshchat.client.views.home.HomeScreenHandler screen = new HomeScreenHandler(stage);
        nav.addScreenHandler(Config.MSG_ICON_PATH, screen);
        nav.addScreenHandler(Config.SETTING_ICON_PATH, new SettingDetailsScreenHandler(stage));
        layout.show();
    }
}
