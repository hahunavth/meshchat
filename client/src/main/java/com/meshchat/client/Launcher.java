package com.meshchat.client;

import com.meshchat.client.views.factories.HomeWindowFactory;
import com.meshchat.client.views.factories.LoginScreenFactory;
import com.meshchat.client.views.navigation.StackNavigation;
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

    private void notifyProcess(double progress) {
        this.notifyPreloader(new Preloader.ProgressNotification(progress));
    }

    private void initScreen() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                {
                    try {
                        // init
                        ModelSingleton modelSingleton = ModelSingleton.getInstance();
                        notifyProcess(0.1);     // set progress bar
                        ModelSingleton.getInstance().stackNavigation = new StackNavigation();
                        // add navigation option
                        notifyProcess(0.2);
                        ModelSingleton.getInstance().stackNavigation.addScreenFactory(StackNavigation.WINDOW_LIST.HOME, new HomeWindowFactory());
                        notifyProcess(0.3);
                        ModelSingleton.getInstance().stackNavigation.addScreenFactory(StackNavigation.WINDOW_LIST.LOGIN, new LoginScreenFactory());
                        // preload screen
                        notifyProcess(0.6);
                        ModelSingleton.getInstance().stackNavigation.preloadScreenHandler(StackNavigation.WINDOW_LIST.LOGIN);
                        notifyProcess(0.8);
                        ModelSingleton.getInstance().stackNavigation.preloadScreenHandler(StackNavigation.WINDOW_LIST.HOME);
                        //
                        notifyProcess(1);
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
    public void init() {
        initScreen();
    }

    /**
     */
    @Override
    public void start(Stage stage) throws IOException {

        try {
            // close all on exit app
//            stage.setOnCloseRequest((event -> {
//                Platform.exit();
//                System.exit(0);
//            }));
            stage.close();

            // After the app is ready, show the stage
            ready.addListener((ov, t, t1) -> {
                if (Boolean.TRUE.equals(t1)) {
                    Platform.runLater(() -> {
                        ModelSingleton.getInstance().stackNavigation.lazyInitialize(stage);
                        // default screen: login
                        ModelSingleton.getInstance().stackNavigation.navigate(StackNavigation.WINDOW_LIST.LOGIN);
                        //
                        ModelSingleton.getInstance().stackNavigation.show();
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // set splash screen
        System.setProperty("javafx.preloader", SplashPreloader.class.getCanonicalName());
        // start
        Application.launch(Launcher.class, args);
    }
}