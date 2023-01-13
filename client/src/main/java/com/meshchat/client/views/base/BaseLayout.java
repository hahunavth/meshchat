package com.meshchat.client.views.base;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Chia 1 mh thành layout gồm nhiều session
 * @param <T> Kiểu của key hashmap <key, session>
 */
public abstract class BaseLayout<T> extends BaseScreenHandler{

    private HashMap<T, Pane> sessionMap;

    /**
     * Layout split into many session
     */
    public BaseLayout(String screenPath) {
        super(screenPath);
    }

    @FXML
    public void initialize() {
        this.sessionMap = new HashMap<>();
    }

    /**
     * Add all session on init subclass
     * @param key define enum key in subclass
     * @param pane session pane
     */
    protected void addSession(T key, Pane pane) {
        this.sessionMap.put(key, pane);
    }

    public Pane getSessionContainer (T key) {
        return this.sessionMap.get(key);
    }

    public void clearSessionContent(T key) {
        this.getSessionContainer(key).getChildren().clear();
    }

    /**
     * Thêm component vào 1 sesion
     * @param key
     * @param screenHandler
     */
    public void addSessionContent(T key, FXMLScreenHandler screenHandler) {
        this.getSessionContainer(key).getChildren().add(screenHandler.getContent());
    }

    /**
     * set giá trị component vào 1 session
     * @param key
     * @param screenHandler
     */
    public void setSessionContent(T key, FXMLScreenHandler screenHandler) {
        this.clearSessionContent(key);
        this.addSessionContent(key, screenHandler);
    }

    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;
    }
}
