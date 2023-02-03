package com.meshchat.client.utils;

import com.meshchat.client.views.base.FXMLScreenHandler;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Map 1 way ObList<A> to ObList<B>
 * @param <A>
 * @param <B>
 */
public abstract class CustomUIBinding<A, B> implements ListChangeListener<A> {

    private ObservableList<B> listOut;
    public CustomUIBinding( ObservableList<B> listOut) {
        this.listOut = listOut;
    }

    @Override
    public void onChanged(Change<? extends A> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (A a : change.getAddedSubList()) {
                    B b = convert(a);
                    Platform.runLater(() -> {
                        listOut.add(b);
                    });
                }
            } else if (change.wasRemoved()) {
                for (A a : change.getAddedSubList()) {
                    B b = convert(a);
                    Platform.runLater(() -> {
                        listOut.remove(b);
                    });
                }
            } else if (change.wasReplaced()) {
                // ...
            }
        }
    }

    public abstract B convert (A a);
}
