package com.meshchat.client.views.factories;

import com.meshchat.client.controllers.BaseController;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.LazyInitialize;
import javafx.stage.Stage;

/**
 * Factory pattern
 * Create a completed window includes:
 *  - Controller
 *  - ScreenHandler
 */
public abstract class ScreenFactory implements LazyInitialize {
    public Stage stage;

    public abstract BaseController getController ();

    public abstract BaseScreenHandler getScreenHandler();

    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;
    }
}
