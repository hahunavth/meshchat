//package com.meshchat.client.launchers;
//
//import com.meshchat.client.views.factories.HomeScreenFactory;
//import com.meshchat.client.views.factories.LoginScreenFactory;
//import com.meshchat.client.views.factories.SettingScreenFactory;
//import com.meshchat.client.views.login.LoginScreenHandler;
//import com.meshchat.client.views.navigation.StackNavigation;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class StackNavigationPreview extends PreviewLauncher{
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) throws IOException {
//        StackNavigation stackNavigation = new StackNavigation();
//
//        stackNavigation.addScreenFactory(StackNavigation.WINDOW_LIST.HOME, new HomeScreenFactory());
//        stackNavigation.addScreenFactory(StackNavigation.WINDOW_LIST.SETTING, new LoginScreenFactory());
//
//        stackNavigation.navigate(StackNavigation.WINDOW_LIST.SETTING);
//        stackNavigation.navigate(StackNavigation.WINDOW_LIST.SETTING);
//        stackNavigation.navigate(StackNavigation.WINDOW_LIST.SETTING);
////        stackNavigation.lazyInitialize(stage);
//
//        stackNavigation.show();
//    }
//}
