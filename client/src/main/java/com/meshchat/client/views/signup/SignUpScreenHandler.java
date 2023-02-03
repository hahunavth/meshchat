package com.meshchat.client.views.signup;

import com.google.inject.Inject;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.interfaces.ISignUpViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class SignUpScreenHandler extends BaseScreenHandler {
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

    private ISignUpViewModel viewModel;

    @Inject
    public SignUpScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, ISignUpViewModel viewModel) {
        super(Config.SIGNUP_PATH, navigation);

        this.viewModel = viewModel; // init viewModel
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
        this.viewModel.initClient(this.address.getText(), Integer.parseInt(this.port.getText()));

        try {
            viewModel.handleSignUp(
                    username.getText(),
                    password.getText(),
                    email.getText()
            );
            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.LOGIN).show();
        } catch (APICallException e) {
            DialogScreenHandler screenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
            screenHandler.getViewModel().setMessage(e.getMessage());
            screenHandler.show();
        }
    }

    /**
     * Go to login screen
     */
    public void onLoginPressed(ActionEvent event) {
        this.getNavigation().goBack().show();
    }

    @Override
    public void show() {
        // close connection when show signup screen
        this.viewModel.closeClient();
        super.show();
    }

    @Override
    public void onShow() {

    }
}
