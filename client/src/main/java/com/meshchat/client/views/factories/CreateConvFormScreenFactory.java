package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.form.CreateConvFormHandler;

public class CreateConvFormScreenFactory implements ScreenFactory<CreateConvFormHandler> {
    @Override
    public CreateConvFormHandler getScreenHandler() {
        return new CreateConvFormHandler();
    }
}
