package com.meshchat.client.observablebinding;

import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class ScreenHandlerToNodeBinding extends CustomUIBinding<FXMLScreenHandler, Node>{
    public ScreenHandlerToNodeBinding(ObservableList<Node> listOut) {
        super(listOut);
    }

    @Override
    public Node convert(FXMLScreenHandler fxmlScreenHandler) {
        return fxmlScreenHandler.getContent();
    }
}
