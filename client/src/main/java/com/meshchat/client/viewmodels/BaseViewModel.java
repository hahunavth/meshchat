package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.views.navigation.StackNavigation;
import jnr.ffi.annotations.In;

/**
 * ViewModel <br>
 * - 2 way binding <br>
 * - process data source (model) <br>
 * - impl action handler <br>
 * <br>
 * - Đối với api call: <br>
 * + Gọi api thông qua TCPNativeClient và nhận response <br>
 * + Dựa vào response để cập nhật DataStore <br>
 */
public abstract class BaseViewModel {
    private DataStore dataStore;
    private TCPNativeClient tcpClient;

    @Inject
    public BaseViewModel(DataStore dataStore, TCPNativeClient client) {
        this.dataStore = dataStore;
        this.tcpClient = client;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public TCPNativeClient getTcpClient() {
        return tcpClient;
    }
}
