package com.meshchat.client.views.base;

import javafx.stage.Stage;

/**
 * INavigation:
 * basic navigation
 * @param <T>
 */
public interface INavigation<T> {
    BaseScreenHandler navigate(T screenName);
    BaseScreenHandler navigate(T screenName, Stage stage);

    BaseScreenHandler goBack();
}

interface ILazyNavigation<K, V> {
    void addScreenFactory(K screenName, ScreenFactory<? extends BaseScreenHandler> screen);
    void preloadScreenHandler(K screenName);

    void lazyInitialize();
}