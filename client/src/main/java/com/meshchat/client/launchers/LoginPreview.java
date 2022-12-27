package com.meshchat.client.launchers;

import com.meshchat.client.views.login.LoginScreenHandler;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPreview extends PreviewLauncher{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        LoginScreenHandler screen = new LoginScreenHandler(stage);
        screen.show();
    }
}
