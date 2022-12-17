package com.meshchat.client.views;

import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeScreenHandler extends BaseScreenHandler {

    public HomeScreenHandler(String fxmlPath) throws IOException {
        super(fxmlPath);
    }

    public HomeScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }
}
