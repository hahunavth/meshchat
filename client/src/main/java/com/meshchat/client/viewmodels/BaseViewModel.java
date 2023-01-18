package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.DataStore;

/**
 * ViewModel
 * - 2 way binding
 * - process data source (model)
 * - impl action handler
 */
public abstract class BaseViewModel {
    protected DataStore dataStore = ModelSingleton.getInstance().dataStore;

}
