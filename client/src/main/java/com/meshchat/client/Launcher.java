package com.meshchat.client;

import com.meshchat.client.views.factories.*;
import com.meshchat.client.views.navigation.StackNavigation;
import com.meshchat.client.views.splash.SplashPreloader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import com.google.inject.Guice;
import com.google.inject.Injector;

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
                        StackNavigation navigation = injector.getInstance(StackNavigation.class);
                        // init
                        notifyProcess(0.1);     // set progress bar

                        // add navigation option
                        notifyProcess(0.2);
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.HOME, new HomeWindowFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.LOGIN, new LoginWindowFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.SIGNUP, new SignUpScreenFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.SEARCH_USER, new SearchUserWindowFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.CREATE_CONV, new CreateConvFormScreenFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.CONV_INFO, new ConvInfoWindowFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.USER_INFO, new UserProfileScreenFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.DIALOG, new DialogScreenFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.SEARCH_CHAT_USER, new SearchChatUserWindowFactory());
                        navigation.addScreenFactory(StackNavigation.WINDOW_LIST.SEARCH_CONV_USER, new SearchConvUserWindowFactory());

                        // preload screen
                        notifyProcess(0.3);
                        navigation.preloadScreenHandler(StackNavigation.WINDOW_LIST.SEARCH_USER);
                        notifyProcess(0.4);
                        navigation.preloadScreenHandler(StackNavigation.WINDOW_LIST.SIGNUP);
                        notifyProcess(0.6);
                        navigation.preloadScreenHandler(StackNavigation.WINDOW_LIST.LOGIN);
                        notifyProcess(0.8);
                        navigation.preloadScreenHandler(StackNavigation.WINDOW_LIST.HOME);

                        // done
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
            StackNavigation navigation = injector.getInstance(StackNavigation.class);

            stage.close();

            // After the app is ready, show the stage
            ready.addListener((ov, t, t1) -> {
                if (Boolean.TRUE.equals(t1)) {
                    Platform.runLater(() -> {
                        navigation.lazyInitialize();
                        // default screen: login
                        navigation.navigate(StackNavigation.WINDOW_LIST.LOGIN).show();
                        //
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // IoC setup
    public static Injector injector = Guice.createInjector(new DIModule());
    public static void main(String[] args) {
        // set splash screen
        System.setProperty("javafx.preloader", SplashPreloader.class.getCanonicalName());
        // start
        Application.launch(Launcher.class, args);
    }
}