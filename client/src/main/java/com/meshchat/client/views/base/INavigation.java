package com.meshchat.client.views.base;

import com.meshchat.client.views.factories.ScreenFactory;

/**
 * INavigation:
 * basic navigation
 * @param <T>
 */
public interface INavigation<T> {
    void addScreenHandler(T screenName, BaseScreenHandler screen);
    void addScreenFactory(T screenName, ScreenFactory screenFactory);
    BaseScreenHandler navigate(T screenName);
}
