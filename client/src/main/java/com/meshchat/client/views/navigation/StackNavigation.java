package com.meshchat.client.views.navigation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.base.ScreenFactory;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Singleton
public class StackNavigation extends FactoryBasedNavigation<StackNavigation.WINDOW_LIST> implements INavigation<StackNavigation.WINDOW_LIST> {

    @Inject
    private TCPNativeClient tcpClient;

    public enum WINDOW_LIST {
        HOME,
        SETTING,
        LOGIN,
        SIGNUP,
        SEARCH_USER,
        CREATE_CONV,
        CONV_INFO,
        USER_INFO,
        DIALOG
    }

    private final Stack<BaseScreenHandler> windowStack = new Stack<>();



    /**
     * Navigate to new screen <br>
     * - if preloaded, get from preloadScreenHandlerMap <br>
     * - if not, create new screen handler <br>
     */
    @Override
    public BaseScreenHandler navigate(WINDOW_LIST screenName) {
        BaseScreenHandler screenHandler;

        if (super.getPreloadScreenHandlerMap().containsKey(screenName)) {
            screenHandler = this.getPreloadScreenHandlerMap().get(screenName);
            this.getPreloadScreenHandlerMap().remove(screenName);
        } else {
            screenHandler = createScreenHandler(screenName);
        }
        // set prev screen
        if (!this.windowStack.isEmpty()) {
            this.windowStack.peek().hide();
        }
        this.windowStack.push(screenHandler);

        if(screenHandler.getStage() == null) {
            screenHandler.lazyInitialize(this.setupNewStage());
        }

        return screenHandler;
    }

    /**
     * Navigate: set stage and navigate <br>
     * @param screenName screen name
     * @param stage lazy initialize stage
     * @return screen handler
     */
    @Override
    public BaseScreenHandler navigate(WINDOW_LIST screenName, Stage stage) {
        BaseScreenHandler screenHandler = navigate(screenName);
        screenHandler.lazyInitialize(stage);

        return screenHandler;
    }

    public BaseScreenHandler goBack() {
        this.windowStack.pop().hide();
        return this.windowStack.peek();
    }

    private Stage setupNewStage () {
        Stage stage = new Stage();
        return this.setupNewStage(stage);
    }

    private Stage setupNewStage (Stage stage) {
        // handle close
        stage.setOnCloseRequest((e) -> {
            if (this.windowStack.size() <= 1) {
                this.tcpClient.close();
                stage.close();
                Platform.exit();
                System.exit(0);
            } else {
                e.consume();
                stage.hide();
                this.goBack().show();
            }
        });

        return stage;
    }

    public void lazyInitialize() {
        Platform.setImplicitExit(false);

        // init child
        this.getPreloadScreenHandlerMap().forEach((i, j) -> {
            j.lazyInitialize(this.setupNewStage());
        });
    }
}
