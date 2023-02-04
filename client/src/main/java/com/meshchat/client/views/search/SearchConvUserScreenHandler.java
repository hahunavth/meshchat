package com.meshchat.client.views.search;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.interfaces.ISearchUserViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import jnr.ffi.annotations.In;

import java.util.ArrayList;

public class SearchConvUserScreenHandler extends SearchUserScreenHandler {

    @Inject
    public SearchConvUserScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, ISearchUserViewModel viewModel) {
        super(navigation, viewModel);

        selectBtn.setOnAction(a -> {
            UserEntity selectedUser = usersTbl.getSelectionModel().getSelectedItem();
            viewModel.addSelectedUser(selectedUser);
            this.getNavigation().goBack().show();
        });

        cancelBtn.setOnAction(a -> {
            viewModel.clearSelectUserViewModel();
            this.getNavigation().goBack().show();
        });
    }


}
