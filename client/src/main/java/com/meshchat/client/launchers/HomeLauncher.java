package com.meshchat.client.launchers;

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

public class HomeLauncher extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
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
            TabsLayout layout = new TabsLayout();
            layout.lazyInitialize(stage);
            TabNavigation nav = new TabNavigation(layout);
            // After fade out, load actual content
            fadeOut.setOnFinished((e) -> {
                layout.setTitle("Home Screen");
                Platform.runLater(layout::show);
            });
            // init screen
            HomeScreenHandler screen = new HomeScreenHandler(stage);
            SettingDetailsScreenHandler setting = new SettingDetailsScreenHandler(stage);
            fadeOut.setOnFinished((e) -> {
                layout.addSessionContent(TabsLayout.Sessions.TAB, nav);
                nav.addScreenHandler(Config.MSG_ICON_PATH, screen);
                nav.addScreenHandler(Config.SETTING_ICON_PATH, setting);
                Platform.runLater(layout::show);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
