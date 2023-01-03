package com.meshchat.client.db.entities;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class MemberEntity implements IEntity{
    private final LongProperty user_id;
    private final LongProperty conv_id;

    public MemberEntity() {
        user_id = new SimpleLongProperty();
        conv_id = new SimpleLongProperty();
    }

    public long getUser_id() {
        return user_id.get();
    }

    public LongProperty user_idProperty() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id.set(user_id);
    }

    public long getConv_id() {
        return conv_id.get();
    }

    public LongProperty conv_idProperty() {
        return conv_id;
    }

    public void setConv_id(long conv_id) {
        this.conv_id.set(conv_id);
    }
}
