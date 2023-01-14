package com.meshchat.client.views.factories;

import com.meshchat.client.views.base.ScreenFactory;
import com.meshchat.client.views.search.SearchUserScreenHandler;

public class SearchUserWindowFactory implements ScreenFactory<SearchUserScreenHandler> {
    @Override
    public SearchUserScreenHandler getScreenHandler() {
        return new SearchUserScreenHandler();
    }
}
