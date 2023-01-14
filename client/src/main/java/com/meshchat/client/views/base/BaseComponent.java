package com.meshchat.client.views.base;

import javafx.fxml.Initializable;

/**
 * Component:
 * - Phần đc tái sử dụng nhiều,
 * - Input props data.
 */
public abstract class BaseComponent<Props> extends FXMLScreenHandler {
    private Props data;
    public BaseComponent(String screenPath) {
        super(screenPath);
    }

    protected void setProps(Props data) {
        this.data = data;
    }
    public Props getProps() {
        return data;
    }
}
