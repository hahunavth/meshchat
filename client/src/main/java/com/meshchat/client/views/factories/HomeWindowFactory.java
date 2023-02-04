package com.meshchat.client.views.factories;

import com.meshchat.client.Launcher;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.form.UserProfileScreenHandler;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.layout.TabsLayout;
import com.meshchat.client.views.navigation.TabNavigation;
import com.meshchat.client.views.settings.SettingDetailsScreenHandler;

/**
 * HomeWindow includes: <br>
 * - Tab bar in left side <br>
 * - Screen: <br>
 *  + Home screen <br>
 *  + Setting screen <br>
 */
public class HomeWindowFactory implements ScreenFactory<BaseScreenHandler> {

    TabsLayout layout;
    TabNavigation navigation;

    @Override
    public TabsLayout getScreenHandler() {

        layout = new TabsLayout();
        navigation = new TabNavigation(layout);

        layout.addSessionContent(TabsLayout.Sessions.TAB, navigation);

        HomeScreenHandler homeScreenHandler = new HomeScreenFactory().getScreenHandler();
        UserProfileScreenHandler userProfileScreenHandler = Launcher.injector.getInstance(UserProfileScreenHandler.class);
        userProfileScreenHandler.getViewModel().setUserId(
                userProfileScreenHandler.getViewModel().getCurrentUserId()
        );

        layout.lazyShowList.add(homeScreenHandler);
        layout.lazyShowList.add(userProfileScreenHandler);

        navigation.addScreenHandler(Config.MSG_ICON_PATH, homeScreenHandler);
        navigation.addScreenHandler(Config.USER_PROFILE_ICON_PATH, userProfileScreenHandler);
//        navigation.addScreenHandler(Config.SETTING_ICON_PATH, Launcher.injector.getInstance(SettingDetailsScreenHandler.class));

        return layout;
    }

}
