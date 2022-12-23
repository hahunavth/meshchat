package com.meshchat.client.views.navigation;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.layout.TabsLayout;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class TabNavigation extends BaseScreenHandler {
    @FXML
    private VBox menuItems;
    private final TabsLayout layout;
    private BaseScreenHandler selectedScreen;
    private List<TabButton> tabButtonList = new ArrayList<>();
    public TabNavigation(Stage stage, TabsLayout layout) {
        super(stage, Config.TAB_NAV);
        this.layout = layout;
        this.layout.getContent().getStylesheets().add(Objects.requireNonNull(getClass().getResource(Config.STYLE_PATH)).toExternalForm());
    }

    public void addMenuItem (String imgPath, BaseScreenHandler screenHandler) {
        TabButton tabButton = new TabButton(this.stage, imgPath);
        if(tabButtonList.size() == 0) {
            tabButton.setSelected(true);
        }
        tabButton.setOnMouseClicked((event) -> {
            layout.setSessionContent(TabsLayout.SCREEN, screenHandler);
            tabButtonList.forEach((btn) -> {
                btn.setSelected(false);
            });
            tabButton.setSelected(true);
        });

        // add to tab bar
        this.menuItems.getChildren().add(tabButton.getContent());
        tabButtonList.add(tabButton);

        if (this.selectedScreen == null) {
            this.selectedScreen = screenHandler;
            layout.setSessionContent(TabsLayout.SCREEN, screenHandler);
        }
    }
}
