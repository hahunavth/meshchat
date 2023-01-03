package com.meshchat.client.views.navigation;

import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.factories.ScreenFactory;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StackNavigation implements INavigation<StackNavigation.WINDOW_LIST> {

    public enum WINDOW_LIST {
        HOME,
        SETTING,
        LOGIN,
        SIGNUP
    }

    public INavigation nested;
    private Stack<BaseScreenHandler> windowStack = new Stack<>();
    private Map<WINDOW_LIST, BaseScreenHandler> preloadScreenHandlerMap = new HashMap<>();
    private Map<WINDOW_LIST, ScreenFactory> screenMap = new HashMap<>();

    @Override
    public void addScreenHandler(WINDOW_LIST screenName, BaseScreenHandler screen) {
        this.preloadScreenHandlerMap.put(screenName, screen);
    }

    /**
     * Add screen factory first
     */
    @Override
    public void addScreenFactory(WINDOW_LIST screenName, ScreenFactory screen) {
        screenMap.put(screenName, screen);
        // create first screen
//        if (this.windowStack.isEmpty()) {
//            this.navigate(screenName);
//        }
    }

    /**
     * Create screen handler from screen factory map
     */
    public BaseScreenHandler createScreenHandler (WINDOW_LIST screenName) {
        ScreenFactory screenFactory = screenMap.get(screenName);

        BaseScreenHandler screenHandler = screenFactory.getScreenHandler();
        screenHandler.setBaseController(screenFactory.getController());

        return screenHandler;
    }

    /**
     * Create screen handler and add to preload map
     */
    public void preloadScreenHandler(WINDOW_LIST screenName) {
        BaseScreenHandler screenHandler = createScreenHandler(screenName);
        preloadScreenHandlerMap.put(screenName, screenHandler);
    }

    /**
     * Navigate to new screen
     * - if preloaded, get from preloadScreenHandlerMap
     * - if not, create new screen handler
     */
    @Override
    public BaseScreenHandler navigate(WINDOW_LIST screenName) {
        BaseScreenHandler screenHandler = null;

        if (preloadScreenHandlerMap.containsKey(screenName)) {
            screenHandler = preloadScreenHandlerMap.get(screenName);
            preloadScreenHandlerMap.remove(screenName);
        } else {
            screenHandler = createScreenHandler(screenName);
        }

        // set prev screen
        if (!this.windowStack.isEmpty()) {
            screenHandler.setPreviousScreen(
                    this.windowStack.peek()
            );
            this.windowStack.peek().hide();
        }

        this.windowStack.push(screenHandler);

        return screenHandler;
    }

    public void goBack() {
        this.windowStack.pop();
        Platform.runLater(this::show);
    }

    private Stage setupNewStage () {
        Stage stage = new Stage();

        // handle close
        stage.setOnCloseRequest((e) -> {
            if (this.windowStack.size() <= 1) {
                stage.close();
                Platform.exit();
            } else {
                e.consume();
                stage.hide();
                this.goBack();
            }
        });

        return stage;
    }

    public void lazyInitialize() {
        Platform.setImplicitExit(false);

        // init child
        this.screenMap.forEach((i, j) -> {
            j.lazyInitialize(this.setupNewStage());
        });
        this.windowStack.forEach((i) -> {
            i.lazyInitialize(this.setupNewStage());
        });
        this.preloadScreenHandlerMap.forEach((i, j) -> {
            j.lazyInitialize(this.setupNewStage());
        });
    }

    public void show () {
        Platform.runLater(() -> {
            this.windowStack.peek().show();
        });
    }
}
