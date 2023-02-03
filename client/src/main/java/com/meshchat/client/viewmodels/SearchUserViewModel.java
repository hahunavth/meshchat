package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.ISearchUserViewModel;

import java.util.ArrayList;

public class SearchUserViewModel extends BaseViewModel implements ISearchUserViewModel {
    @Inject
    public SearchUserViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public ArrayList<UserEntity> handleSearch(String searchTxt, int limit, int offset){
        TCPNativeClient client = this.getTcpClient();
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
