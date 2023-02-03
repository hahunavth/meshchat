package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IDialogViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DialogViewModel extends BaseViewModel implements IDialogViewModel {
    private final StringProperty message = new SimpleStringProperty();

    @Inject
    public DialogViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public StringProperty getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
