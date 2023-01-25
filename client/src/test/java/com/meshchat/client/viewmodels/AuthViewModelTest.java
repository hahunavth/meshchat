package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test signup and login view model
 */
class AuthViewModelTest {

    SignUpViewModel viewModel;
    LoginViewModel loginViewModel;

    @BeforeEach
    void setUp() {
        viewModel = new SignUpViewModel();
        loginViewModel = new LoginViewModel();
        // connect
        ModelSingleton.getInstance().initClient("127.0.0.1", 9000);
    }
    @Test
    void handleSignUp() {
        // sign up new account
        boolean stt = viewModel.handleSignUp("abc", "def", "a@b.c");
        assertTrue(stt);
    }

    @Test
    void handleLogin() {
        boolean stt = loginViewModel.handleLogin("abc", "def");
        assertTrue(stt);
    }
}