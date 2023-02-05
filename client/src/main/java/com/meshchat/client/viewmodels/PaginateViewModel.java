package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.viewmodels.interfaces.IPaginateViewModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

/**
 * Control sth pageable
 */
public class PaginateViewModel implements IPaginateViewModel {
    private IntegerProperty page = new SimpleIntegerProperty();
    private IntegerProperty pageSize = new SimpleIntegerProperty();

    @Inject
    public PaginateViewModel() {
        this.pageSize.set(10);
        this.resetPage();
    }

    public PaginateViewModel(int pageSize) {
        this.pageSize.set(pageSize);
        this.resetPage();
    }

    @Override
    public void addPageNoChangeLister(ChangeListener<Number> e) {
        this.page.addListener(e);
    }

    @Override
    public void addPageSizeChangeListener(ChangeListener<Number> e) {
        this.pageSize.addListener(e);
    }

    @Override
    public void bindPageNo(IntegerProperty v) {
        this.page.bindBidirectional(v);
    }

    @Override
    public void goToNextPage() throws Exception {
        if (page.get() >= 1)
            this.page.set(this.page.get() - 1);
        else
            throw new Exception("Cannot go to page < 1");
    }

    @Override
    public void goToPrevPage() {
        this.page.set(this.page.get() + 1);
    }

    @Override
    public void resetPage() {
        this.page.set(1);
    }

    @Override
    public void setPageSize(int size) {
        this.pageSize.set(size);
    }

    @Override
    public int getPageNo() {
        return this.page.get();
    }

    @Override
    public int getPageSize() {
        return this.pageSize.get();
    }
}
