package com.meshchat.client.views.login;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginScreenHandler extends BaseScreenHandler {

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button login;
    @FXML
    private Button signup;

    public LoginScreenHandler(Stage stage) {
        super(Config.LOGIN_PATH);
    }
}
