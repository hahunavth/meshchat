package com.meshchat.client.model;

public class User implements ISchema {
    private long id;
    private String username;
    private String password;
    private String phone_number;
    private String email;

    public User(long id, String username, String password, String phone_number, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone_number = phone_number;
        this.email = email;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
