package com.meshchat.client.controllers;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.DataSource;
import com.meshchat.client.net.providers.IApiProvider;
import com.meshchat.client.views.base.BaseScreenHandler;

/**
 * Handle business logic for screen handler
 */
public abstract class BaseController<S extends BaseScreenHandler> {
    protected IApiProvider apiProvider = ModelSingleton.getInstance().apiProvider;
    protected DataSource dataSource = ModelSingleton.getInstance().dataSource;

    private S screenHandler;

    public S getScreenHandler() {
        return this.screenHandler;
    }

    public void setScreenHandler(S screenHandler) {
        this.screenHandler = screenHandler;

        // NOTE: override and handle logic here
    }
}
