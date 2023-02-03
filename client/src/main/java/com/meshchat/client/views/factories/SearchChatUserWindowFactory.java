package com.meshchat.client.views.factories;

import com.meshchat.client.Launcher;
import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.search.SearchChatUserScreenHandler;

public class SearchChatUserWindowFactory implements ScreenFactory<SearchChatUserScreenHandler> {
    @Override
    public SearchChatUserScreenHandler getScreenHandler() {
        return Launcher.injector.getInstance(SearchChatUserScreenHandler.class);
    }
}
