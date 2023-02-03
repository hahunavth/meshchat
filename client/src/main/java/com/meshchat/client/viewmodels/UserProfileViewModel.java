package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IUserProfileViewModel;

public class UserProfileViewModel extends BaseViewModel implements IUserProfileViewModel {
    @Inject
    public UserProfileViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public UserEntity getUserEntity() {
        return this.getDataStore().getUserProfile().getEntity();
    }
}
