package com.meshchat.client.views.splash;

import com.meshchat.client.utils.Config;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Objects;

public class SplashPreloader extends Preloader {

    Stage stage;

    @FXML
    private ProgressBar progressBar;
    private Scene scene;
    boolean noLoadingProgress = true;

//    private Scene createPreloaderScene() {
//        bar = new ProgressBar(0);
//        BorderPane p = new BorderPane();
//        p.setCenter(bar);
//        return new Scene(p, 300, 150);
//    }

    public SplashPreloader() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.SPLASH_PATH));
            loader.setController(this);
            Parent root = loader.load();
            this.scene = new Scene(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        // add bootstrapfx library stylesheet
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        this.stage.setScene(this.scene);
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.show();
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        //application loading progress is rescaled to be first 50%
        //Even if there is nothing to load 0% and 100% events can be
        // delivered
        if (pn.getProgress() != 1.0 || !noLoadingProgress) {
            progressBar.setProgress(pn.getProgress()/2);
            if (pn.getProgress() > 0) {
                noLoadingProgress = false;
            }
        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof ProgressNotification) {
            //expect application to send us progress notifications
            //with progress ranging from 0 to 1.0
            double v = ((ProgressNotification) pn).getProgress();
            if (!noLoadingProgress) {
                //if we were receiving loading progress notifications
                //then progress is already at 50%.
                //Rescale application progress to start from 50%
                v = 0.5 + v/2;
            }
            progressBar.setProgress(v);
        } else if (pn instanceof StateChangeNotification) {
            //hide after get any state update from application
            if (((StateChangeNotification) pn).getType() == StateChangeNotification.Type.BEFORE_START)
                stage.hide();
        }
    }

    @Override
    public void handleStateChangeNotification(Preloader.StateChangeNotification info) {
        // ignore
//        StateChangeNotification.Type type = info.getType();
//        switch (type) {
//            case BEFORE_START -> {
//                stage.hide();
//            }
//        }
    }
}
