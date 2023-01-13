//package com.meshchat.client.launchers;
//
//import com.meshchat.client.views.layout.TabsLayout;
//import javafx.application.Platform;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class TabLayoutLaunch extends PreviewLauncher {
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) throws IOException {
//
//        stage.setTitle("TabLayout");
//        TabsLayout layout = new TabsLayout();
//        layout.lazyInitialize(stage);
//
//        layout.getSessionContainer(TabsLayout.Sessions.TAB).setBackground(
//                this.getBackground(1)
//        );
//        layout.getSessionContainer(TabsLayout.Sessions.SCREEN).setBackground(
//                this.getBackground(4)
//        );
//
//        Platform.runLater(layout::show);
//    }
//}
