package com.meshchat.client.views.base;

import com.meshchat.client.controllers.BaseController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

/**
 * For routing
 */
public class BaseScreenHandler extends FXMLScreenHandler {

    protected final Stage stage;            // if not exists -> create new, exists -> pass through constructor on init
    private Scene scene;                    // if not exists -> create new when show screen
    private BaseScreenHandler prev;         // prev screen
    private BaseController controller;      // current controller

    public BaseScreenHandler(String fxmlPath) throws IOException {
        super(fxmlPath);
        this.stage = new Stage();
    }

    public BaseScreenHandler(Stage stage, String screenPath) throws IOException {
        super(screenPath);
        this.stage = stage;
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
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public void setTitle(String title) {
        this.stage.setTitle(title);
    }

    public BaseController getBaseController() {
        return controller;
    }

    public void setBaseController(BaseController controller) {
        this.controller = controller;
    }
}
