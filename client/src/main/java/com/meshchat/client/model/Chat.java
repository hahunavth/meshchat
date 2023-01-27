package com.meshchat.client.model;

import com.meshchat.client.db.entities.UserEntity;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Chat extends ChatGen {
    public long id;
    private UserEntity user2;

    public Chat () {
    }

    public Chat(UserEntity user2) {
        this.user2 = user2;
    }

    public void setUser2(UserEntity user2) {
        this.user2 = user2;
    }

    public UserEntity getUser2() {
        return user2;
    }

    @Override
    public StringProperty getName() {
        return this.user2.usernameProperty();
    }

    @Override
    public String toString() {
        return super.toString() + "{ " + "\n" +
                "\t id:" + id + "\n" +
                "\t user2_id:" + user2.getId() + "\n" +
                "\t user2_email:" + user2.getEmail() + "\n" +
                "\t user2_uname:" + user2.getUsername() + "\n" +
                "\t user2_phone:" + user2.getPhone_number() + "\n" +
                "}\n";
    }
}
