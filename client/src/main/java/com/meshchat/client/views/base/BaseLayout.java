package com.meshchat.client.views.base;

import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public abstract class BaseLayout extends BaseScreenHandler {

    private final HashMap<String, Pane> sessionMap = new HashMap<>();

    /**
     * Layout split into many session
     */
    public BaseLayout(Stage stage, String screenPath) {
        super(stage, screenPath);
    }

    /**
     * Add all session on init subclass
     * @param key define enum key in subclass
     * @param pane session pane
     */
    protected void addSession(String key, Pane pane) {
        this.sessionMap.put(key, pane);
    }

    public Pane getSessionContainer (String key) {
        return this.sessionMap.get(key);
    }

    public void clearSessionContent(String key) {
        this.getSessionContainer(key).getChildren().clear();
    }

    public void addSessionContent(String key, FXMLScreenHandler screenHandler) {
        this.getSessionContainer(key).getChildren().add(screenHandler.getContent());
    }

    public void setSessionContent(String key, FXMLScreenHandler screenHandler) {
        this.clearSessionContent(key);
        this.addSessionContent(key, screenHandler);
    }
}
