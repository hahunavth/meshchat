package com.meshchat.client.views.factories;

import com.meshchat.client.Launcher;
import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.search.SearchConvUserScreenHandler;

public class SearchConvUserWindowFactory implements ScreenFactory<SearchConvUserScreenHandler> {
    @Override
    public SearchConvUserScreenHandler getScreenHandler() {
        return Launcher.injector.getInstance(SearchConvUserScreenHandler.class);
    }
}
