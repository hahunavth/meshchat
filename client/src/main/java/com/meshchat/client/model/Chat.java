package com.meshchat.client.model;

import com.meshchat.client.db.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class Chat extends ChatGen {
    private final UserEntity user2;

    public Chat(UserEntity user2) {
        this.user2 = user2;
    }

    public UserEntity getUser2() {
        return user2;
    }
}
