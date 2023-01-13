package com.meshchat.client.views.layout;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseLayout;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * HomeLayout
 * Gồm 3 phàn:
 * - Sidebar
 * - Content
 * - Info
 */
public class HomeLayout extends BaseLayout<HomeLayout.Sessions> {
    @FXML
    private Pane sidebar;
    @FXML
    private Pane content;
    @FXML
    private Pane info;

    public enum Sessions {
        SIDEBAR,
        CONTENT,
        INFO
    }

    public HomeLayout(Stage stage) {
        super(Config.HOME_LAYOUT_PATH);
    }

    /**
     * Thêm các pane vào layout
     */
    @FXML
    public void initialize() {
        super.initialize();
        super.addSession(Sessions.SIDEBAR, this.sidebar);
        super.addSession(Sessions.CONTENT, this.content);
        super.addSession(Sessions.INFO, this.info);
    }
}
