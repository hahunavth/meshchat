package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.DataSource;
import com.meshchat.client.views.base.BaseScreenHandler;

/**
 * ViewModel
 * - 2 way binding
 * - process data source (model)
 * - impl action handler
 */
public abstract class BaseViewModel {
    protected DataSource dataSource = ModelSingleton.getInstance().dataSource;

}
