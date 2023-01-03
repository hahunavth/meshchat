package com.meshchat.client.db.entities;

import javafx.beans.property.*;

/**
 * DATABASE ENTITY
 */
public class UserEntity implements IEntity {
    private final LongProperty id;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty phone_number;
    private final StringProperty email;

    public UserEntity() {
        id = new SimpleLongProperty();
        username = new SimpleStringProperty();
        password = new SimpleStringProperty();
        phone_number = new SimpleStringProperty();
        email = new SimpleStringProperty();
    }

    public UserEntity(long id, String username, String phone_number, String email) {
        this();
        this.setId(id);
        this.setUsername(username);
        this.setPhone_number(phone_number);
        this.setEmail(email);
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

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getPhone_number() {
        return phone_number.get();
    }

    public StringProperty phone_numberProperty() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number.set(phone_number);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }
}
