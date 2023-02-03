package com.meshchat.client.views.form;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.interfaces.IUserProfileViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileScreenHandler extends BaseScreenHandler implements Initializable {

    @FXML
    private Label uname;

    @FXML
    private Label phone;

    @FXML
    private Label email;

    IUserProfileViewModel viewModel;

    @Inject
    public UserProfileScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, IUserProfileViewModel viewModel) {
        super(Config.USER_PROFILE_PATH, navigation);

        this.viewModel = viewModel;
        UserEntity user = viewModel.getUserEntity();
        uname.setText(user.getUsername());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void onShow() {

    }
}
