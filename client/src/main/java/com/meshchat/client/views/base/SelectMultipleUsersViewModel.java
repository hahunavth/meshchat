package com.meshchat.client.views.base;

import com.meshchat.client.db.entities.UserEntity;

import java.util.ArrayList;

public interface SelectMultipleUsersViewModel extends SelectUserViewModel{
    ArrayList<UserEntity> getSelectedUsers();
}
