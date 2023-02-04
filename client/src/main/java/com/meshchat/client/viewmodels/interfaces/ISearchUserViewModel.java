package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.views.base.SelectMultipleUsersViewModel;
import com.meshchat.client.views.base.SelectSingleUserViewModel;
import com.meshchat.client.views.base.SelectUserViewModel;

import java.util.List;

public interface ISearchUserViewModel extends SelectSingleUserViewModel {
    List<UserEntity> handleSearch(String searchTxt, int limit, int offset);

    // TODO: SelectSingleUserViewModel or SelectMultipleUsersViewModel ???
    void handleCreateChat(UserEntity user);
    void setSelectUserViewModel(SelectUserViewModel viewModel);
    void clearSelectUserViewModel();
    void addSelectedUser(UserEntity user);
}
