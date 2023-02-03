module com.meshchat.client {
    requires java.logging;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires org.objectweb.asm;
    requires org.jnrproject.ffi;
    requires org.jnrproject.jffi;

    requires com.google.guice;

    opens com.meshchat.client.net.client to com.google.guice;
    opens com.meshchat.client to javafx.fxml, com.google.guice;
    opens com.meshchat.client.viewmodels to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.layout to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.home to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.settings to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.navigation to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.splash to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.base to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.components to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.login to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.signup to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.dialog to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.search to javafx.fxml, com.google.guice;
    opens com.meshchat.client.views.form to javafx.fxml, com.google.guice;

    exports com.meshchat.client;
    exports com.meshchat.client.cnative;
    exports com.meshchat.client.viewmodels;
    exports com.meshchat.client.views.layout;
    exports com.meshchat.client.views.home;
    exports com.meshchat.client.views.dialog;
    exports com.meshchat.client.views.settings;
    exports com.meshchat.client.views.navigation;
    exports com.meshchat.client.views.base;
    exports com.meshchat.client.views.components;
    exports com.meshchat.client.views.splash;
    exports com.meshchat.client.views.search;
    exports com.meshchat.client.views.form;
    exports com.meshchat.client.model;
    exports com.meshchat.client.viewmodels.interfaces;
    opens com.meshchat.client.viewmodels.interfaces to com.google.guice, javafx.fxml;
}