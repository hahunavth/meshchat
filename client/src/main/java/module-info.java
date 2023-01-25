module com.meshchat.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires org.objectweb.asm;
    requires org.jnrproject.ffi;
    requires org.jnrproject.jffi;

//    requires java.sql;
//    requires java.sql.rowset;
//    requires ojdbc8;
//    requires com.sun.jna;
//    requires com.sun.xml.bind;
//    requires com.sun.xml.bind.core;
//    requires org.testfx.api;

    opens com.meshchat.client to javafx.fxml;
    opens com.meshchat.client.viewmodels to javafx.fxml;
//    opens com.meshchat.client.launchers to javafx.fxml;
    opens com.meshchat.client.views.layout to javafx.fxml;
    opens com.meshchat.client.views.home to javafx.fxml;
    opens com.meshchat.client.views.settings to javafx.fxml;
    opens com.meshchat.client.views.navigation to javafx.fxml;
    opens com.meshchat.client.views.splash to javafx.fxml;
    opens com.meshchat.client.views.base to javafx.fxml;
    opens com.meshchat.client.views.components to javafx.fxml;
    opens com.meshchat.client.views.login to javafx.fxml;
    opens com.meshchat.client.views.signup to javafx.fxml;
   opens com.meshchat.client.views.dialog to javafx.fxml;


    exports com.meshchat.client;
    exports com.meshchat.client.cnative;
    exports com.meshchat.client.viewmodels;
//    exports com.meshchat.client.launchers;
    exports com.meshchat.client.views.layout;
    exports com.meshchat.client.views.home;
    exports com.meshchat.client.views.dialog;
    exports com.meshchat.client.views.settings;
    exports com.meshchat.client.views.navigation;
    exports com.meshchat.client.views.base;
    exports com.meshchat.client.views.components;
    exports com.meshchat.client.views.splash;
}