package com.meshchat.client.views.navigation;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import com.meshchat.client.views.base.BaseLayout;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.components.TabButton;
import com.meshchat.client.views.factories.ScreenFactory;
import com.meshchat.client.views.layout.TabsLayout;
import javafx.fxml.FXML;
import javafx.scene.layout.*;

import java.util.*;

public class TabNavigation extends BaseComponent implements INavigation<TabButton> {
    @FXML
    private VBox menuItems;
    private final BaseLayout layout;
    private TabButton selectedBtn;
//    private final List<TabButton> tabButtonList = new ArrayList<>();
    private final Map<TabButton, BaseScreenHandler> screenHandlerMap = new HashMap<>();

    public TabNavigation(BaseLayout layout) {
        super(Config.TAB_NAV);
        this.layout = layout;
        // Add style
        this.layout.getContent().getStylesheets().add(Objects.requireNonNull(getClass().getResource(Config.STYLE_PATH)).toExternalForm());
    }

    public void addScreenHandler(String imgPath, BaseScreenHandler screenHandler) {
        TabButton tabButton = new TabButton(imgPath);
        this.addScreenHandler(tabButton, screenHandler);
    }

    @Override
    public void addScreenHandler(TabButton tabButton, BaseScreenHandler screenHandler) {
        screenHandlerMap.put(tabButton, screenHandler);

        // select first screen as default
        if(screenHandlerMap.size() == 1) {
            tabButton.setSelected(true);
            navigate(tabButton);
        }

        tabButton.setOnMouseClicked((event) -> {
            navigate(tabButton);
        });

        // add to tab bar
        this.menuItems.getChildren().add(tabButton.getContent());
    }

    public void addScreenFactory(TabButton tabButton, ScreenFactory screenFactory) {
        BaseScreenHandler screenHandler = screenFactory.getScreenHandler();
        this.addScreenHandler(tabButton, screenHandler);
    }

    public void addScreenFactory(String imgPath, ScreenFactory screenFactory) {
        BaseScreenHandler screenHandler = screenFactory.getScreenHandler();
        this.addScreenHandler(imgPath, screenHandler);
    }

    @Override
    public BaseScreenHandler navigate(TabButton screenName) {
        BaseScreenHandler screenHandler = screenHandlerMap.get(screenName);

        // reset color of current selected btn
        if (this.selectedBtn != null) {
            this.selectedBtn.setSelected(false);
        }

        // set new btn
        this.selectedBtn = screenName;
        this.selectedBtn.setSelected(true);

        // set content
        layout.setSessionContent(TabsLayout.Sessions.SCREEN, screenHandler);

        return screenHandler;
    }

}
