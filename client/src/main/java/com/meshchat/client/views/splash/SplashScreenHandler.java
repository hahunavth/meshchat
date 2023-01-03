package com.meshchat.client.views.splash;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @deprecated
 * - Show stage in constructor
 */
public class SplashScreenHandler extends FXMLScreenHandler {

    @FXML
    private ProgressBar progressBar;

//    private static ProgressBar statProgressBar;
    private Stage stage;

//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        statProgressBar = progressBar;
//    }

    public SplashScreenHandler(Stage stage) {
        super(Config.SPLASH_PATH);
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(this.content);
        stage.setScene(scene);
        stage.setTitle("Loading...");
    }

//    private FadeTransition fadeOut;
//    private FadeTransition fadeIn;
//
//    public SplashScreenHandler(Stage stage) {
//        super(stage, Config.SPLASH_PATH);
//
//            Scene scene = new Scene(this.content);
//            stage.setScene(scene);
//            stage.setTitle("Loading...");
//
//            // Load splash screen with fade in effect
//            fadeIn = new FadeTransition(Duration.seconds(0.5), this.content);
//            fadeIn.setFromValue(0);
//            fadeIn.setToValue(1);
//            fadeIn.setCycleCount(1);
//
//            // Finish splash with fade out effect
//            fadeOut = new FadeTransition(Duration.seconds(0.5), this.content);
//            fadeOut.setFromValue(1);
//            fadeOut.setToValue(0);
//            fadeOut.setCycleCount(1);
//    }
//
//    public void startAnimation(EventHandler<ActionEvent> init, EventHandler<ActionEvent> finished) {
//        stage.show();
//        // After fade in, start fade out
//        fadeIn.setOnFinished(e -> {
//            init.handle(e);
//            fadeOut.play();
//        });
//        fadeOut.setOnFinished(finished);
//        fadeIn.play();
//    }

}
