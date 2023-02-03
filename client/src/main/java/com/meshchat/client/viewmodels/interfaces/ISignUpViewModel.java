package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.exceptions.APICallException;

public interface ISignUpViewModel {
    void handleSignUp(String uname, String pass, String email) throws APICallException;

    void initClient(String host, int port);

    void closeClient();
}
