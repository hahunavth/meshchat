package com.meshchat.client.views.signup;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.SignUpViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.event.ActionEvent;
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
    private TextField email;
    @FXML
    private Button a; // TODO: rename to login
    @FXML
    private Button signup;

    private SignUpViewModel viewModel;

    public SignUpScreenHandler() {
        super(Config.SIGNUP_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.viewModel = new SignUpViewModel(); // init viewModel
        this.accord.setExpandedPane(this.acc_tiled_pane); // expand acc pane first

        signup.setOnAction(this::onSignupPressed);
        a.setOnAction(this::onLoginPressed);
    }

    /**
     * on submit pressed
     * 1. connect server
     * 2. send signup request
     * 3. navigate to login screen or show failed dialog
     */
    public void onSignupPressed(ActionEvent event) {
        ModelSingleton.getInstance().initClient(this.address.getText(), Integer.parseInt(this.port.getText()));
        if(viewModel.handleSignUp(
                username.getText(),
                password.getText(),
                email.getText()
        )) {
            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.LOGIN).show();
        } else {
            DialogScreenHandler screenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
            screenHandler.getViewModel().setMessage("Register failed!");
            screenHandler.show();
        }
    }

    /**
     * Go to login screen
     */
    public void onLoginPressed(ActionEvent event) {
        ModelSingleton.getInstance().stackNavigation.goBack().show();
    }

    @Override
    public void show() {
        // close connection when show signup screen
        ModelSingleton.getInstance().tcpClient.close();
        super.show();
    }

    @Override
    public void onShow() {

    }
}
