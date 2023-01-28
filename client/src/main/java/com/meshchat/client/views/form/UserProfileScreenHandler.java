package com.meshchat.client.views.form;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
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

    public UserProfileScreenHandler() {
        super(Config.USER_PROFILE_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserEntity user = ModelSingleton.getInstance().dataStore.getUserProfile().getEntity();
        uname.setText(user.getUsername());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
    }

    @Override
    public void onShow() {

    }
}
