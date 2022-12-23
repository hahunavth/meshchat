package com.meshchat.client.views.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;

/**
 * NOTE: Do not set fx:controller in .fxml files
 * <br/><s>fx:controller="com.meshchat.client.views.HomeScreenHandler"</s>
 */
public abstract class FXMLScreenHandler {
    protected FXMLLoader loader;
    protected Parent content;
    /**
     * @param screenPath Đường dẫn đến file .fxml
     *  - Đặt file trong thư mục: /resources/${package_name}
     *  - Lấy filePath không tình /resources
     *  - VD: /resource/com/meshchat/client/views/a.fxml -> fxmlPath = "/com/meshchat/client/views/a.fxml"
     */
    public FXMLScreenHandler(String screenPath) {
        // Load fxml
        this.loader = new FXMLLoader(getClass().getResource(screenPath));
        // Set this class as the controller -> not depend on fxml file definition
        this.loader.setController(this);
        try {
            this.content = loader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public Parent getContent() {
        return this.content;
    }

    public FXMLLoader getLoader() {
        return this.loader;
    }
}
