package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.Launcher;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.net.client.TCPNativeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test signup and login view model
 */
class AuthViewModelTest {

    @Inject
    TCPNativeClient client;

    SignUpViewModel viewModel;
    LoginViewModel loginViewModel;

    @BeforeEach
    void setUp() {
        viewModel = Launcher.injector.getInstance(SignUpViewModel.class);
        loginViewModel = Launcher.injector.getInstance(LoginViewModel.class);
        // connect
        client = Launcher.injector.getInstance(TCPNativeClient.class);
//        client.initClient("127.0.0.1", 9000);
        client.initClient("13.230.248.35", 9000);
    }
    @Test
    void handleSignUp() throws APICallException {
        // sign up new account
        viewModel.handleSignUp("abc", "def", "a@b.c");
    }

    @Test
    void handleLogin() {
        boolean stt = loginViewModel.handleLogin("abc", "def");
        assertTrue(stt);
    }
}