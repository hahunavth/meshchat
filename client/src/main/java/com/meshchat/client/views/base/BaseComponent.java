package com.meshchat.client.views.base;

/**
 * Component:
 * - Phần đc tái sử dụng nhiều,
 * - Bỏ bớt style, ... không cần thiết để tăng hiệu năng
 */
public abstract class BaseComponent extends FXMLScreenHandler {
    public BaseComponent(String screenPath) {
        super(screenPath);
    }
}
