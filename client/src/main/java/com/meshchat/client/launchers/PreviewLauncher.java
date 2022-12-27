package com.meshchat.client.launchers;

import com.meshchat.client.Launcher;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

/**
 * Launch để test view
 */
public abstract class PreviewLauncher extends Launcher {

    private static String[] imageUrls = new String[] {
            "https://edencoding.com/wp-content/uploads/2021/03/layer_06_1920x1080.png",
            "https://edencoding.com/wp-content/uploads/2021/03/layer_05_1920x1080.png",
            "https://edencoding.com/wp-content/uploads/2021/03/layer_04_1920x1080.png",
            "https://edencoding.com/wp-content/uploads/2021/03/layer_03_1920x1080.png",
            "https://edencoding.com/wp-content/uploads/2021/03/layer_02_1920x1080.png",
            "https://edencoding.com/wp-content/uploads/2021/03/layer_01_1920x1080.png"
    };
    public static BackgroundImage createImage(String url) {
        return new BackgroundImage(
                new Image(url),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
    }

    public Background getBackground(int i) {
        int id = i % imageUrls.length;
        return new Background(
                createImage(imageUrls[i])
        );
    }

}
