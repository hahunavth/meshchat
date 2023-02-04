package com.meshchat.client.viewmodels.interfaces;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;

public interface IPaginateViewModel {
    void addPageNoChangeLister(ChangeListener<Number> e);

    void addPageSizeChangeListener(ChangeListener<Number> e);

    void bindPageNo(IntegerProperty v);

    void goToNextPage() throws Exception;

    void goToPrevPage();

    void resetPage();

    void setPageSize(int size);

    int getPageNo();

    int getPageSize();

    default int getOffset() {
        return (this.getPageNo() - 1) * this.getPageSize();
    }
}
