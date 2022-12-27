package com.meshchat.client;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.base.BaseLayout;
import com.meshchat.client.views.factories.HomeScreenFactory;
import com.meshchat.client.views.factories.SettingScreenFactory;
import com.meshchat.client.views.layout.TabsLayout;
import com.meshchat.client.views.navigation.TabNavigation;
import com.meshchat.client.views.settings.SettingDetailsScreenHandler;
import com.meshchat.client.views.splash.SplashPreloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {


    BooleanProperty ready = new SimpleBooleanProperty(false);

    BaseLayout layout;
    TabNavigation navigation;
    HomeScreenHandler home;
    SettingDetailsScreenHandler setting;

    private void initScreen() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                {
                    try {
                        ModelSingleton modelSingleton = ModelSingleton.getInstance();

                        notifyPreloader(new Preloader.ProgressNotification(0.1));
                        layout = new TabsLayout();

                        notifyPreloader(new Preloader.ProgressNotification(0.2));
                        navigation = new TabNavigation(layout);

                        notifyPreloader(new Preloader.ProgressNotification(0.3));
                        layout.addSessionContent(TabsLayout.Sessions.TAB, navigation);

                        notifyPreloader(new Preloader.ProgressNotification(0.4));
                        HomeScreenFactory homeScreenFactory = new HomeScreenFactory();
                        notifyPreloader(new Preloader.ProgressNotification(0.5));
                        SettingScreenFactory settingScreenFactory = new SettingScreenFactory();

                        notifyPreloader(new Preloader.ProgressNotification(0.6));
                        home = homeScreenFactory.getScreenHandler();
                        notifyPreloader(new Preloader.ProgressNotification(0.7));
                        setting = settingScreenFactory.getScreenHandler();
//
                        notifyPreloader(new Preloader.ProgressNotification(0.9));
                        navigation.addScreenHandler(Config.MSG_ICON_PATH, home);
                        notifyPreloader(new Preloader.ProgressNotification(1));
                        navigation.addScreenHandler(Config.SETTING_ICON_PATH, setting);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // After init is ready, the app is ready to be shown
                // Do this before hiding the preloader stage to prevent the
                // app from exiting prematurely
                ready.setValue(Boolean.TRUE);

                notifyPreloader(new Preloader.StateChangeNotification(
                        Preloader.StateChangeNotification.Type.BEFORE_START));

                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public void init() throws IOException {
        initScreen();
    }

    /**
     */
    @Override
    public void start(Stage stage) throws IOException {

        try {
            // close all on exit app
            stage.setOnCloseRequest((event -> {
                Platform.exit();
                System.exit(0);
            }));

            // After the app is ready, show the stage
            ready.addListener((ov, t, t1) -> {
                if (Boolean.TRUE.equals(t1)) {
                    Platform.runLater(() -> {
                        this.layout.lazyInitialize(stage);
                        this.home.lazyInitialize(stage);
                        this.setting.lazyInitialize(stage);
                        this.layout.show();
                    });
                }
            });;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashPreloader.class.getCanonicalName());
        Application.launch(Launcher.class, args);
    }
}