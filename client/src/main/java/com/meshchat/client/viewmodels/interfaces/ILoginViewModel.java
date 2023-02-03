package com.meshchat.client.viewmodels.interfaces;

public interface ILoginViewModel {
    boolean handleLogin(String uname, String pass);

    void initClient(String host, int port);

    void closeClient();
}
