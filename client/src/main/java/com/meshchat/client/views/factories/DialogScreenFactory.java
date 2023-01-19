package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DialogScreenFactory implements ScreenFactory<DialogScreenHandler> {


    @Override
    public DialogScreenHandler getScreenHandler() {
        return new DialogScreenHandler();
    }
}
