package com.meshchat.client.views.layout;

import com.meshchat.client.utils.Config;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeLayout extends BaseLayout {
    @FXML
    private Pane sidebar;
    @FXML
    private Pane content;
    @FXML
    private Pane info;

    public static final String SIDEBAR = "SIDEBAR";
    public static final String CONTENT = "CONTENT";
    public static final String INFO = "INFO";

    public HomeLayout(Stage stage) throws IOException {
        super(stage, Config.HOME_LAYOUT_PATH);
        super.addSession(SIDEBAR, this.sidebar);
        super.addSession(CONTENT, this.content);
        super.addSession(INFO, this.info);
    }
}
