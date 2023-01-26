package com.meshchat.client.views.base;

import javafx.stage.Stage;

/**
 * LazyInitialize: set stage and init after constructor
 */
public interface LazyInitialize {
    void lazyInitialize(Stage stage);

    void onShow();
}
