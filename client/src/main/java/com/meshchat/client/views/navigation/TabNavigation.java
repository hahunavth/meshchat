package com.meshchat.client.views.navigation;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.layout.BaseLayout;
import com.meshchat.client.views.layout.TabsLayout;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class TabNavigation extends BaseScreenHandler {
    @FXML
    private VBox menuItems;
    private final TabsLayout layout;
    private BaseScreenHandler home;
    public TabNavigation(Stage stage, TabsLayout layout) throws IOException {
        super(stage, Config.TAB_NAV);
        this.layout = layout;
    }

    public void addMenuItem (String imgPath, BaseScreenHandler screenHandler) {
        // crate new button
        ImageView btn = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(imgPath)).toExternalForm()));
        btn.setPickOnBounds(true);
        // add to screen list
//        this.screenList.add(this)
        // set size
        btn.setFitWidth(36);
        btn.setFitHeight(36);
        // set event on click
        btn.setOnMouseClicked((event) -> {
            layout.setSessionContent(TabsLayout.SCREEN, screenHandler);
        });
        // add to tab bar
        this.menuItems.getChildren().add(btn);

        if (this.home == null) {
            this.home = screenHandler;
            layout.setSessionContent(TabsLayout.SCREEN, screenHandler);
        }
    }
}
