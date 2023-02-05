package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Conv;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IConvInfoViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ConvInfoViewModel extends BaseViewModel implements IConvInfoViewModel {

    private long convId;

    private ObservableList<UserEntity> addedUsers = FXCollections.observableArrayList(new ArrayList<>());

    @Inject
    public ConvInfoViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public void setConvId(long convId) {
        this.convId = convId;
    }

    @Override
    public Conv getConvInfo() throws APICallException {
        return this.getTcpClient()._get_conv_info(convId);
    }

    @Override
    public ObservableList<UserEntity> getSelectedUsers() {
        return this.addedUsers;
    }

    @Override
    public void addSelectedUser(UserEntity user) {
        this.addedUsers.add(user);
    }

    @Override
    public void clearSelectedUser() {
        this.addedUsers.clear();
    }

    @Override
    public void addMember(long user2Id) throws APICallException {
        this.getTcpClient()._conv_join(convId, user2Id);
    }
}
