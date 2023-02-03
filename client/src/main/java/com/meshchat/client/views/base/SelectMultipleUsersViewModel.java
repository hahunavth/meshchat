package com.meshchat.client.views.base;

import com.meshchat.client.db.entities.UserEntity;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public interface SelectMultipleUsersViewModel extends SelectUserViewModel{
    ObservableList<UserEntity> getSelectedUsers();
}
