package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.db.entities.UserEntity;

public interface IUserProfileViewModel {
    UserEntity getUserEntity();
    long getCurrentUserId();
    UserEntity fetchUserProfile(long uid);
    void setUserId(long userId);

    long getUserId();
}
