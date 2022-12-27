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

public class StackNavigation implements INavigation<StackNavigation.WINDOW_LIST>, LazyInitialize {

    public enum WINDOW_LIST {
        HOME,
        SETTING,
        LOGIN,
        SIGNUP
    }


    public INavigation nested;
    public Stack<BaseScreenHandler> windowStack = new Stack<>();
    public Map<WINDOW_LIST, ScreenFactory> screenMap = new HashMap<>();

    @Override
    public void addScreenHandler(WINDOW_LIST screenName, BaseScreenHandler screen) {
//        screenMap.put(screenName, screen);
    }

    @Override
    public void addScreenFactory(WINDOW_LIST screenName, ScreenFactory screen) {
        screenMap.put(screenName, screen);
        // create first screen
        if (this.windowStack.isEmpty()) {
            this.navigate(screenName);
        }
    }

    @Override
    public BaseScreenHandler navigate(WINDOW_LIST screenName) {
        ScreenFactory screenFactory = screenMap.get(screenName);

        BaseScreenHandler screenHandler = screenFactory.getScreenHandler();
        screenHandler.setBaseController(screenFactory.getController());
        if (!this.windowStack.isEmpty()) {
            screenHandler.setPreviousScreen(
                    this.windowStack.peek()
            );
        }

        this.windowStack.push(screenHandler);

        return screenHandler;
    }

    public void goBack() {
        this.windowStack.pop();
        this.show();
    }

    @Override
    public void lazyInitialize(Stage stage) {
        Platform.setImplicitExit(false);
        // handle close

        // init child
        this.screenMap.forEach((i, j) -> {
            if (j instanceof LazyInitialize) {
                Stage stage1 = new Stage();
                stage1.setOnCloseRequest((e) -> {
                    if (this.windowStack.size() <= 1) {
                        stage1.close();
                    } else {
                        e.consume();
                        stage1.hide();
                        this.goBack();
                    }
                });
                ((LazyInitialize) j).lazyInitialize(stage1);
            }
        });
        this.windowStack.forEach((i) -> {
            if (i != null) {
                Stage stage1 = new Stage();
                stage1.setOnCloseRequest((e) -> {
                    if (this.windowStack.size() <= 1) {
                        stage1.close();
                    } else {
                        e.consume();
                        stage1.hide();
                        this.goBack();
                    }
                });
                ((LazyInitialize) i).lazyInitialize(stage1);
            }
        });
    }

    public void show () {
        this.windowStack.peek().show();
    }
}
