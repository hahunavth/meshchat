package com.meshchat.client.views.base;

import com.meshchat.client.db.entities.UserEntity;

public interface SelectSingleUserViewModel extends SelectUserViewModel{
    public UserEntity getSelectedUser();
}
