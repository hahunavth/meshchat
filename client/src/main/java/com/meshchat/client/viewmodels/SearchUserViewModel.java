package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.ISearchUserViewModel;
import com.meshchat.client.views.base.SelectUserViewModel;

import java.util.ArrayList;

public class SearchUserViewModel extends BaseViewModel implements ISearchUserViewModel {
    @Inject
    public SearchUserViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }
    private SelectUserViewModel selectUserViewModel = null;

    public void handleCreateChat(UserEntity user){
        try {
            this.getTcpClient()._create_chat(user.getId());
        } catch (APICallException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UserEntity> handleSearch(String searchTxt, int limit, int offset){
        TCPNativeClient client = this.getTcpClient();
        ArrayList<UserEntity> res = new ArrayList<>();
        try {
            long[] ls = client._get_user_search(searchTxt, limit, offset);
            for(long id : ls){
                // fixme: sometime fn return null
                UserEntity ue = client._get_user_by_id(id);
                if (ue != null)
                    res.add(ue);
            }
            return res;
        } catch (APICallException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void clearSelectUserViewModel() {
        selectUserViewModel.clearSelectedUser();
        selectUserViewModel = null;
    }

    public void setSelectUserViewModel(SelectUserViewModel viewModel){
        selectUserViewModel = viewModel;
    }

    public void addSelectedUser(UserEntity user){
        selectUserViewModel.addSelectedUser(user);
    }

    @Override
    public void clearSelectedUser() {

    }

    @Override
    public UserEntity getSelectedUser() {
        return null;
    }
}
