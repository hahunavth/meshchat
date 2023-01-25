package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;

public class SignUpViewModel extends BaseViewModel{
    public boolean handleSignUp(String uname, String pass, String email) {
        return ModelSingleton.getInstance().tcpClient._register(
                uname, pass, "23456789", email
        );
    }
}
