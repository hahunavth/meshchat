package com.meshchat.client.viewmodels;


import com.google.inject.Inject;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.ILoginViewModel;

public class LoginViewModel extends BaseViewModel implements ILoginViewModel {
    @Inject
    public LoginViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public boolean handleLogin(String uname, String pass) {
        return this.getTcpClient()._login(uname, pass);
    }

    public void initClient(String host, int port) {
        this.getTcpClient().initClient(host, port);
    }

    public void closeClient() {
        this.getTcpClient().close();
    }
}
