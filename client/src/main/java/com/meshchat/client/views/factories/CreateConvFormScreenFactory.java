package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.form.CreateConvForm;

public class CreateConvFormScreenFactory implements ScreenFactory<CreateConvForm> {
    @Override
    public CreateConvForm getScreenHandler() {
        return new CreateConvForm();
    }
}
