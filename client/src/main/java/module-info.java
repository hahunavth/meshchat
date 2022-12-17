module com.meshchat.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

//    requires org.testfx.api;

    opens com.meshchat.client to javafx.fxml;
    opens com.meshchat.client.controllers to javafx.fxml;
    opens com.meshchat.client.launchers to javafx.fxml;
    opens com.meshchat.client.views.layout to javafx.fxml;
//    opens com.meshchat.client.views.components to javafx.fxml;
    opens com.meshchat.client.views.home to javafx.fxml;
    opens com.meshchat.client.views.settings to javafx.fxml;

    exports com.meshchat.client;
    exports com.meshchat.client.controllers;
    exports com.meshchat.client.launchers;
    exports com.meshchat.client.views;
    exports com.meshchat.client.views.layout;
    exports com.meshchat.client.views.home;
    exports com.meshchat.client.views.settings;
    exports com.meshchat.client.views.navigation;
    opens com.meshchat.client.views.navigation to javafx.fxml;

}