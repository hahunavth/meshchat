package com.meshchat.client.views.signup;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpScreenHandler extends BaseScreenHandler implements Initializable {
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
    private Button a; // signin
    @FXML
    private Button signup;

    public SignUpScreenHandler() {
        super(Config.SIGNUP_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.accord.setExpandedPane(this.acc_tiled_pane);
//        signup.setOnAction((a) -> {
//            ModelSingleton.getInstance().initClient(this.address.getText(), Integer.parseInt(this.port.getText()));
//            System.out.println(this.port.getText());
//
//            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.HOME).show();
//        });
        a.setOnAction((a) -> {
            ModelSingleton.getInstance().stackNavigation.goBack().show();
        });
    }

    @Override
    public void show() {
        ModelSingleton.getInstance().tcpClient.close();
        super.show();
    }
}
