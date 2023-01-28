package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.net.client.TCPNativeClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public class CreateConvViewModel extends BaseViewModel{

    private HashSet<UserEntity> selectedUsers;

    public long handleCreate(String gname)  {
        TCPNativeClient client = ModelSingleton.getInstance().tcpClient;
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
        for(UserEntity u : selectedUsers){
            ls.add(u);
        }
        return ls;
    }
}
