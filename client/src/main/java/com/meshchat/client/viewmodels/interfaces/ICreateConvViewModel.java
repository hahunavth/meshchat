package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.db.entities.UserEntity;
import javafx.collections.ObservableList;

public interface ICreateConvViewModel {
    long handleCreate(String gname);

    void addSelectedUsers(UserEntity user);

    ObservableList<UserEntity> getSelectedUsers();
}
