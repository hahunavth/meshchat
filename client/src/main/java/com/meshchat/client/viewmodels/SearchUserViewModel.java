package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.net.client.TCPNativeClient;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchUserViewModel extends BaseViewModel{
    public ArrayList<UserEntity> handleSearch(String searchTxt, int limit, int offset){
        TCPNativeClient client = ModelSingleton.getInstance().tcpClient;
        ArrayList<UserEntity> res = new ArrayList<>();
        try {
            long[] ls = client._get_user_search(searchTxt, limit, offset);
            for(long id : ls){
                res.add(client._get_user_by_id(id));
            }
            return res;
        } catch (APICallException e) {
            e.printStackTrace();
        }
        return res;
    }
}
