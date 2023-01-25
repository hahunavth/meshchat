package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.cnative.CAPIServiceLib;
import jnr.ffi.Runtime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignUpViewModelTest {

    SignUpViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new SignUpViewModel();
        // connect
        ModelSingleton.getInstance().initClient("127.0.0.1", 9000);
    }
    @Test
    void handleSignUp() {
        // sign up new account
        boolean stt = viewModel.handleSignUp("abc", "def", "a@b.c");
        assertTrue(stt);
    }
}