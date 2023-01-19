package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import org.controlsfx.control.PropertySheet;

public class SignUpViewModel extends BaseViewModel{
    public boolean handleSignUp(String uname, String pass, String email) {
        ModelSingleton.getInstance().initClient("127.0.0.1", 9000);

        return ModelSingleton.getInstance().tcpClient._register(
                uname, pass, "23456789", email
        );
    }
}
