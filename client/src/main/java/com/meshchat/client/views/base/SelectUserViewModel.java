package com.meshchat.client.views.base;

import com.meshchat.client.db.entities.UserEntity;

public interface SelectUserViewModel {
    public void addSelectedUser(UserEntity user);
    public void clearSelectedUser();
}
