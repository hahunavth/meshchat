package com.meshchat.client.views.base;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.controllers.ChatController;
import com.meshchat.client.utils.Config;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * BaseScreenHandler:
 * - 1 session in layout
 * or
 * - 1 window
 */
public abstract class BaseScreenHandler extends FXMLScreenHandler implements LazyInitialize, IShowable {

    protected Stage stage;            // if not exists -> create new, exists -> pass through constructor on init
    private Scene scene;                    // if not exists -> create new when show screen

    public BaseScreenHandler(String screenPath) {
        super(screenPath);
    }

    /**
     * show
     * Load css and display screen in current stage
     */
    public void show() {
        if (this.scene == null) {
            this.scene = new Scene(this.content);
        }
        // add bootstrapfx library stylesheet
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        // load scrollbar css
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource(Config.SCROLL_BAR_STYLE_PATH)).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource(Config.TAB_STYLE_PATH)).toExternalForm());
        this.stage.setScene(this.scene);
        Platform.runLater(() -> {
            this.stage.show();
        });
    }

    public void hide() {
        this.stage.hide();
    }

    public void setTitle(String title) {
        this.stage.setTitle(title);
    }

    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;
    }
}
