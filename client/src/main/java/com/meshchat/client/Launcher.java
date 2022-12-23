package com.meshchat.client;

import com.meshchat.client.net.TCPClient;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.home.HomeScreenHandler;
import com.meshchat.client.views.layout.TabsLayout;
import com.meshchat.client.views.navigation.TabNavigation;
import com.meshchat.client.views.settings.SettingDetailsScreenHandler;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class Launcher extends Application {

    public static TCPClient tcpClient;

    /**
     * Create tcp client
     */
    private void startTcpClient () {
        tcpClient = new TCPClient();
        // close on exit
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
            tcpClient.close();
            System.out.println("Close connection!");
        }});

        Thread clientThread = new Thread(tcpClient);
        clientThread.start();
    }

    /**
     * Init GUI
     */
    @Override
    public void start(Stage stage) throws IOException {
        try {
            startTcpClient();

            // close all on exit app
            stage.setOnCloseRequest((event -> {
                    Platform.exit();
                    System.exit(0);
            }));

            // initialize the scene
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Config.SPLASH_PATH)));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Loading...");
            stage.show();

            // Load splash screen with fade in effect
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);

            // Finish splash with fade out effect
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            // After fade in, start fade out
            fadeIn.play();
            fadeIn.setOnFinished((e) -> {
                fadeOut.play();
            });

            // layout and navigation
            TabsLayout layout = new TabsLayout(stage);
            TabNavigation nav = new TabNavigation(stage, layout);
            layout.addSessionContent(TabsLayout.TAB, nav);
            HomeScreenHandler screen = new HomeScreenHandler(stage);
            SettingDetailsScreenHandler setting = new SettingDetailsScreenHandler(stage);

            // After fade out, load actual content
            fadeOut.setOnFinished((e) -> {
                layout.setTitle("Mesh chat");
                nav.addMenuItem(Config.MSG_ICON_PATH, screen);
                nav.addMenuItem(Config.SETTING_ICON_PATH, setting);
                layout.show();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}