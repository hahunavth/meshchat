package com.meshchat.client.views.components;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseComponent;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TabButton extends BaseComponent {
    @FXML
    private ImageView img;
    @FXML
    private Region btn;
    private boolean isSelected;

    public TabButton(String iconPath) {
        super(Config.TAB_LAYOUT_BUTTON_PATH);
        Image image = new Image(Objects.requireNonNull(getClass().getResource(iconPath)).toExternalForm());

        img.setImage(image);
    }

    public void setOnMouseClicked( EventHandler<? super MouseEvent> onClick) {
        btn.setOnMouseClicked(onClick);
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (this.isSelected) {
            this.btn.setStyle("-fx-background-color: #5785F5; -fx-background-radius: 8px;");
        } else {
            this.btn.setStyle("-fx-background-color: #ffffff;");
        }
    }

    public boolean getSelected () {
        return this.isSelected;
    }
}
