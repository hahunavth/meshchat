package com.meshchat.client.viewmodels.interfaces;

import javafx.beans.property.StringProperty;

public interface IDialogViewModel {
    StringProperty getMessage();

    void setMessage(String message);
}
