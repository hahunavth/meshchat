package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.ICreateConvViewModel;

import java.util.ArrayList;
import java.util.HashSet;

public class CreateConvViewModel extends BaseViewModel implements ICreateConvViewModel {
    private HashSet<UserEntity> selectedUsers;
    @Inject
    public CreateConvViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public long handleCreate(String gname)  {
        TCPNativeClient client = this.getTcpClient();
        long conv_id = 0;
        try {
            conv_id =  client._create_conv(gname);
            for(UserEntity u : selectedUsers){
                client._conv_join(conv_id, u.getId());
            }
        } catch (APICallException e) {
            e.printStackTrace();
        }
        return conv_id;
    }

    public void addSelectedUsers(UserEntity user){
        selectedUsers.add(user);
    }


    @Override
    public ArrayList<UserEntity> getSelectedUsers() {
        ArrayList<UserEntity> userls = new ArrayList<>();
        selectedUsers.forEach(user->{
            userls.add(user);
        });
        return userls;
    }

    @Override
    public void addSelectedUser(UserEntity user) {
        selectedUsers.add(user);
    }

    @Override
    public void clearSelectedUser() {
        selectedUsers.clear();
    }
}
