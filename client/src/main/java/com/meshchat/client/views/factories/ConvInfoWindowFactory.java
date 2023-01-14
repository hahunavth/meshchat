package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.form.ConvInfoScreenHandler;

public class ConvInfoWindowFactory implements ScreenFactory<ConvInfoScreenHandler> {
    @Override
    public ConvInfoScreenHandler getScreenHandler() {
        return new ConvInfoScreenHandler();
    }
}
