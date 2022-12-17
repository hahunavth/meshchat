package com.meshchat.client.launchers;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.layout.TabsLayout;
import javafx.animation.FadeTransition;
import javafx.application.Application;
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
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        // Finish splash with fade out effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        // After fade in, start fade out
        fadeIn.play();
        fadeIn.setOnFinished((e) -> {
            fadeOut.play();
        });

        TabsLayout handler = new TabsLayout(stage);
//             After fade out, load actual content
        fadeOut.setOnFinished((e) -> {
            handler.setTitle("Home Screen");
            handler.show();
        });
//            FXMLLoader loader1 = new FXMLLoader(Objects.requireNonNull(getClass().getResource(Config.LAYOUT_PATH)));
//            fadeOut.setOnFinished((e) -> {
//                try {
//                    AnchorPane root1 = loader1.load();
//                    Scene scene1 = new Scene(root1);
//                    stage.setScene(scene1);
//                    stage.setTitle("Home");
//                    stage.show();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            });




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
