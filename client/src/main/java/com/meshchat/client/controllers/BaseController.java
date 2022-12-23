package com.meshchat.client.controllers;

import com.meshchat.client.net.TCPClient;
import javafx.event.Event;
import javafx.event.EventHandler;

public abstract class BaseController<T> {
    TCPClient client;

    public BaseController(TCPClient client) {
        this.client = client;
    }

//    public abstract void fetch(EventHandler handler);
//    public abstract void dispatch(Object act);
}
