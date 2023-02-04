package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IUserProfileViewModel;

public class UserProfileViewModel extends BaseViewModel implements IUserProfileViewModel {
    private UserEntity entity;

    @Inject
    public UserProfileViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public UserEntity getUserEntity() {
        return this.entity;
    }

    public UserEntity fetchUserProfile(long uid) {
        return this.getTcpClient()._get_user_by_id(uid);
    }

    long userId;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }

    public long getCurrentUserId() {
        return this.getTcpClient().get_uid();
    }
}
