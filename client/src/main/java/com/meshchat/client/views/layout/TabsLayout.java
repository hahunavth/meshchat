package com.meshchat.client.views.layout;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class TabsLayout extends BaseLayout {

    @FXML
    private Pane tab;
    @FXML
    private Pane screen;

    public static final String TAB = "TAB";
    public static final String SCREEN = "SCREEN";

    public TabsLayout(Stage stage) throws IOException {
        super(stage, Config.TAB_LAYOUT_PATH);
        super.addSession(TabsLayout.TAB, this.tab);
        super.addSession(TabsLayout.SCREEN, this.screen);
    }


//    @FXML
//    private Pane tabsView;
//
//    @FXML
//    private Pane menuView;
//
//    @FXML
//    private Pane detailsView;
//
//    @FXML
//    private Pane infoView;

//    public TabsLayout(Stage stage, String screenPath) throws IOException {
//        super(stage, screenPath);
//        TabsScreenHandler tabs = new TabsScreenHandler(stage);
//        setTabsView(tabs);
//    }

//    public TabsLayout(Stage stage) throws IOException {
//        super(stage, Config.LAYOUT_PATH);
////        TabNavigation tabs = new TabNavigation(stage, this);
////        ChatScreenHandler chat = new ChatScreenHandler(stage);
////        MessageFlowScreenHandler msg = new MessageFlowScreenHandler(stage);
////        setTabsView(tabs);
////        tabs.setPreviousScreen(this);
////        setMenuView(chat);
////        chat.setPreviousScreen(tabs);
////        setDetailsView(msg);
////        msg.setPreviousScreen(chat);
//    }

//    public void clearAllView () {
////        this.tabsView.getChildren().clear();
//        this.menuView.getChildren().clear();
//        this.detailsView.getChildren().clear();
//        this.infoView.getChildren().clear();
//    }

//    public void setTabsView(FXMLScreenHandler screenHandler) {
//        this.tabsView.getChildren().clear();
//        this.tabsView.getChildren().add(screenHandler.getContent());
//    }
//
//    public void setMenuView(FXMLScreenHandler screenHandler) {
//        this.menuView.getChildren().clear();
//        this.menuView.getChildren().add(screenHandler.getContent());
//    }
//
//    public void setDetailsView(FXMLScreenHandler screenHandler) {
//        this.detailsView.getChildren().clear();
//        this.detailsView.getChildren().add(screenHandler.getContent());
//    }
//
//    public void setInfoView(FXMLScreenHandler screenHandler) {
//        this.infoView.getChildren().clear();
//        this.infoView.getChildren().add(screenHandler.getContent());
//    }
}
