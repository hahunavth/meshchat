package com.meshchat.client.views.navigation;

import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.base.ScreenFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Navigation <br>
 * -
 * @param <T>
 */
public abstract class FactoryBasedNavigation<T> implements INavigation<T> {
    private final Map<T, BaseScreenHandler> preloadScreenHandlerMap = new HashMap<>();
    private final Map<T, ScreenFactory<? extends BaseScreenHandler>> screenMap = new HashMap<>();

    public void addScreenFactory(T screenName, ScreenFactory<? extends BaseScreenHandler> screen) {
        this.screenMap.put(screenName, screen);
    }

    /**
     * Create screen handler from screen factory
     *
     * @param screenName name of registered screen factory
     */
    public BaseScreenHandler createScreenHandler (T screenName) {
        return screenMap.get(screenName).getScreenHandler();
    }

    /**
     * Create screen handler and add to preload map <br>
     *
     * @param screenName name of registered screen factory
     */
    public void preloadScreenHandler(T screenName) {
        BaseScreenHandler screenHandler = createScreenHandler(screenName);
        preloadScreenHandlerMap.put(screenName, screenHandler);
    }

    protected Map<T, BaseScreenHandler> getPreloadScreenHandlerMap() {
        return this.preloadScreenHandlerMap;
    }

    protected Map<T, ScreenFactory<? extends BaseScreenHandler>> getScreenMap() {
        return screenMap;
    }
}
