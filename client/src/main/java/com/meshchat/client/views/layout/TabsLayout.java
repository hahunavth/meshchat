package com.meshchat.client.views.layout;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseLayout;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TabsLayout extends BaseLayout {

    @FXML
    private Pane tab;
    @FXML
    private Pane screen;

    public static final String TAB = "TAB";
    public static final String SCREEN = "SCREEN";

    public TabsLayout(Stage stage) {
        super(stage, Config.TAB_LAYOUT_PATH);
        super.addSession(TabsLayout.TAB, this.tab);
        super.addSession(TabsLayout.SCREEN, this.screen);
    }
}
