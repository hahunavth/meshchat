package com.meshchat.client.views.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.meshchat.client.Launcher;
import com.meshchat.client.binding.IStackNavigation;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.util.Objects;

/**
 * BaseScreenHandler:
 * - 1 session in layout
 * or
 * - 1 window
 */
public abstract class BaseScreenHandler extends FXMLScreenHandler implements LazyInitialize, IShowable {

    @Inject
    public INavigation<StackNavigation.WINDOW_LIST> navigation;

    protected Stage stage;            // if not exists -> create new, exists -> pass through constructor on init
    private Scene scene;                    // if not exists -> create new when show screen

    public BaseScreenHandler(String screenPath) {
        super(screenPath);
    }

    public BaseScreenHandler(String screenPath, INavigation<StackNavigation.WINDOW_LIST> navigation) {
        super(screenPath);
        this.navigation = navigation;
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
//        this.onShow();
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

    public INavigation<StackNavigation.WINDOW_LIST> getNavigation() {
        return navigation;
    }

    public Stage getStage() {
        return this.stage;
    }
}
