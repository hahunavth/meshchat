package com.meshchat.client.views.base;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.controllers.ChatController;
import com.meshchat.client.utils.Config;
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
public abstract class BaseScreenHandler<T extends BaseController> extends FXMLScreenHandler implements LazyInitialize {

    protected Stage stage;            // if not exists -> create new, exists -> pass through constructor on init
    private Scene scene;                    // if not exists -> create new when show screen
    private BaseScreenHandler prev;         // prev screen
    private T controller;      // current controller

    public BaseScreenHandler(String screenPath) {
        super(screenPath);
    }

    public void setPreviousScreen(BaseScreenHandler prev) {
        this.prev = prev;
    }

    public BaseScreenHandler getPreviousScreen() {
        return this.prev;
    }

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
        this.stage.show();
    }

    public void setTitle(String title) {
        this.stage.setTitle(title);
    }

    public T getBaseController() {
        return controller;
    }

    public void setBaseController(T controller) {
        this.controller = controller;
        this.controller.setScreenHandler(this);
    }

    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;
    }
}
