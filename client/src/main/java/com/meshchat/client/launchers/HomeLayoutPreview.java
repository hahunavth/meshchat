package com.meshchat.client.launchers;

import com.meshchat.client.views.layout.HomeLayout;
import com.meshchat.client.views.layout.TabsLayout;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeLayoutPreview extends PreviewLauncher {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        HomeLayout layout = new HomeLayout(primaryStage);

        layout.getSessionContainer(HomeLayout.SIDEBAR).setBackground(
                this.getBackground(1)
        );
        layout.getSessionContainer(HomeLayout.INFO).setBackground(
                this.getBackground(3)
        );
        layout.getSessionContainer(HomeLayout.CONTENT).setBackground(
                this.getBackground(5)
        );

        layout.show();
    }
}
