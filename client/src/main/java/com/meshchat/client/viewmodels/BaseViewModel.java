package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.DataStore;

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
    protected DataStore dataStore = ModelSingleton.getInstance().dataStore;

}
