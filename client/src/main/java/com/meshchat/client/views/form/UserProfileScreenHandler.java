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

public class UserProfileScreenHandler extends BaseScreenHandler {

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
    }

    public IUserProfileViewModel getViewModel() {
        return this.viewModel;
    }



    @Override
    public void onShow() {
        long userId = this.viewModel.getUserId();
        if (userId == 0) {
            userId = this.viewModel.getCurrentUserId();
        }
        UserEntity user =
                this.viewModel.fetchUserProfile(userId);
        uname.textProperty().bind(user.usernameProperty());
        phone.textProperty().bind(user.phoneProperty());
        email.textProperty().bind(user.emailProperty());
    }

    @Override
    public void show() {
        super.show();
//        this.onShow();
    }
}
