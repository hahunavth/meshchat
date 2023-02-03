package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.views.base.SelectMultipleUsersViewModel;
import javafx.collections.ObservableList;

public interface ICreateConvViewModel extends SelectMultipleUsersViewModel {
    long handleCreate(String gname);

    void addSelectedUsers(UserEntity user);

}
