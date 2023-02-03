package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.db.entities.UserEntity;

import java.util.ArrayList;

public interface ISearchUserViewModel {
    ArrayList<UserEntity> handleSearch(String searchTxt, int limit, int offset);
}
