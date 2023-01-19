package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;

public class LoginViewModel extends BaseViewModel{
    public boolean handleLogin(String host, int port, String uname, String pass) {
        ModelSingleton.getInstance().initClient(host, port);

        return ModelSingleton.getInstance().tcpClient._login(uname, pass);
    }
}
