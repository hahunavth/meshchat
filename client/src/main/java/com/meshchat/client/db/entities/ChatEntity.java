package com.meshchat.client.db.entities;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class ChatEntity implements IEntity {
    private final LongProperty id;
    private final LongProperty member1;
    private final LongProperty member2;

    public ChatEntity () {
        id = new SimpleLongProperty();
        member1 = new SimpleLongProperty();
        member2 = new SimpleLongProperty();
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public long getMember1() {
        return member1.get();
    }

    public LongProperty member1Property() {
        return member1;
    }

    public void setMember1(long member1) {
        this.member1.set(member1);
    }

    public long getMember2() {
        return member2.get();
    }

    public LongProperty member2Property() {
        return member2;
    }

    public void setMember2(long member2) {
        this.member2.set(member2);
    }
}
