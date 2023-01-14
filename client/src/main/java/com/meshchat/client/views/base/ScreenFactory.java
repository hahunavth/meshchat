package com.meshchat.client.views.base;

/**
 * Using factory method to lazy load screen.
 *
 * @see INavigation
 */
public interface ScreenFactory<T extends BaseScreenHandler> {
    T getScreenHandler();
}
