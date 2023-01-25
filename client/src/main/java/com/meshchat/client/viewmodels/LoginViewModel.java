package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;

public class LoginViewModel extends BaseViewModel{
    public boolean handleLogin(String uname, String pass) {
        return ModelSingleton.getInstance().tcpClient._login(uname, pass);
    }
}
