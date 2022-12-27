package com.meshchat.client.views.home;

import com.meshchat.client.views.base.LazyInitialize;
import com.meshchat.client.views.layout.HomeLayout;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenHandler extends HomeLayout implements LazyInitialize {

    List<LazyInitialize> lazyComponents = new ArrayList<>();
    public HomeScreenHandler(Stage stage) {
        super(stage);
    }

    public HomeScreenHandler() {
        super(null);
    }

    @Override
    public void lazyInitialize(Stage stage) {
        this.stage = stage;

        this.stage.setTitle("Home");
        this.lazyComponents.forEach((item) -> item.lazyInitialize(stage));
    }
}
