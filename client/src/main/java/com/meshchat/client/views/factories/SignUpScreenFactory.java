package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.signup.SignUpScreenHandler;

public class SignUpScreenFactory implements ScreenFactory<SignUpScreenHandler> {
    @Override
    public SignUpScreenHandler getScreenHandler() {
        return new SignUpScreenHandler();
    }
}
