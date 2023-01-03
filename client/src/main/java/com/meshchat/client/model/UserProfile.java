package com.meshchat.client.model;

import com.meshchat.client.db.entities.UserEntity;

public class UserProfile extends BaseSchema {
    private final UserEntity entity = new UserEntity();

    public UserProfile(long id, String username, String password, String phone_number, String email) {
        this.entity.setId(id);
        this.entity.setUsername(username);
        this.entity.setPassword(password);
        this.entity.setPhone_number(phone_number);
        this.entity.setEmail(email);
    }

    public UserProfile(long id, String username, String phone_number, String email) {
        this.entity.setId(id);
        this.entity.setUsername(username);
        this.entity.setPhone_number(phone_number);
        this.entity.setEmail(email);
    }

    public UserProfile() {
    }

    public UserEntity getEntity() {
        return entity;
    }
}
