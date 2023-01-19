package com.meshchat.client.viewmodels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DialogViewModel extends BaseViewModel {
    private final StringProperty message = new SimpleStringProperty();

    public DialogViewModel(String message) {
        this.message.set(message);
    }

    public StringProperty getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
