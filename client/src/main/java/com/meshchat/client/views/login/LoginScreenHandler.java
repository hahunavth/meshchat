package com.meshchat.client.views.login;

import com.google.inject.Inject;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.interfaces.ILoginViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginScreenHandler extends BaseScreenHandler {

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
    private ILoginViewModel viewModel;

    @Inject
    public LoginScreenHandler(ILoginViewModel viewModel) {
        super(Config.LOGIN_PATH);

        this.viewModel = viewModel;
        this.accord.setExpandedPane(this.acc_tiled_pane);
        login.setOnAction(this::onLoginClicked);

        signup.setOnAction((a) -> {
            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.SIGNUP).show();
        });
    }

    public void onLoginClicked(Event e) {
        this.viewModel.initClient(this.address.getText(), Integer.parseInt(this.port.getText()));

        if (
                this.viewModel.handleLogin(this.username.getText(), this.password.getText())
        ) {
            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.HOME).show();
        } else {
            DialogScreenHandler dialogScreenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
            dialogScreenHandler.getViewModel().setMessage("Cannot login");
            dialogScreenHandler.show();
        }
    }

    @Override
    public void show() {
        this.viewModel.closeClient();
        super.show();
    }

    @Override
    public void onShow() {

    }
}
