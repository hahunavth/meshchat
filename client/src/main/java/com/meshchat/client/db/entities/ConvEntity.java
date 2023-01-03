package com.meshchat.client.db.entities;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConvEntity implements IEntity {
    private final LongProperty id;
    private final LongProperty admin_id;
    private final StringProperty name;

    public ConvEntity() {
        id = new SimpleLongProperty();
        admin_id = new SimpleLongProperty();
        name = new SimpleStringProperty();
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

    public long getAdmin_id() {
        return admin_id.get();
    }

    public LongProperty admin_idProperty() {
        return admin_id;
    }

    public void setAdmin_id(long admin_id) {
        this.admin_id.set(admin_id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
