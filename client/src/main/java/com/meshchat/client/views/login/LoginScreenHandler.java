package com.meshchat.client.views.login;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.LoginViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginScreenHandler extends BaseScreenHandler implements Initializable {

    @FXML
    private Accordion accord;
    @FXML
    private TitledPane acc_tiled_pane;

    @FXML
    private TextField address;
    @FXML
    private TextField port;

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button login;
    @FXML
    private Button signup;
    private LoginViewModel viewModel;

    public LoginScreenHandler() {
        super(Config.LOGIN_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.viewModel = new LoginViewModel();
        this.accord.setExpandedPane(this.acc_tiled_pane);
        login.setOnAction((a) -> {
//            ModelSingleton.getInstance().initClient("127.0.0.1", 9000);
//            System.out.println(this.port.getText());
            if (
                this.viewModel.handleLogin("127.0.0.1", 9000, this.username.getText(), this.password.getText())
            ) {
                this.getNavigation().navigate(StackNavigation.WINDOW_LIST.HOME).show();
            } else {
                DialogScreenHandler dialogScreenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
                dialogScreenHandler.getViewModel().setMessage("Cannot login");
                dialogScreenHandler.show();
            }
        });

        signup.setOnAction((a) -> {
            ModelSingleton.getInstance().stackNavigation.navigate(StackNavigation.WINDOW_LIST.SIGNUP).show();
        });
    }

    @Override
    public void show() {
        ModelSingleton.getInstance().tcpClient.close();
        super.show();
    }
}
