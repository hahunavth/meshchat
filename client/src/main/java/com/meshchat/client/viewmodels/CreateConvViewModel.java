package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.ICreateConvViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public ObservableList<UserEntity> getSelectedUsers(){
        ObservableList<UserEntity> ls = FXCollections.observableArrayList();
        if (selectedUsers != null) {
            for(UserEntity u : selectedUsers){
                ls.add(u);
            }
        }
        return ls;
    }
}
