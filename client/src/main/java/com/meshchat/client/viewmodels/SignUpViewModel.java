package com.meshchat.client.viewmodels;


import com.google.inject.Inject;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.ISignUpViewModel;

public class SignUpViewModel extends BaseViewModel implements ISignUpViewModel {
    @Inject
    public SignUpViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public void handleSignUp(String uname, String pass, String email) throws APICallException {
        this.getTcpClient()._register(
                uname, pass, "23456789", email
        );
    }

    public void initClient(String host, int port) {
        this.getTcpClient().initClient(host, port);
    }

    public void closeClient() {
        this.getTcpClient().close();
    }
}
