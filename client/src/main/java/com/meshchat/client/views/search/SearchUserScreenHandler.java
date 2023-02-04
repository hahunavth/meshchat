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

import java.util.ArrayList;
import java.util.List;

public class SearchUserScreenHandler extends BaseScreenHandler {

    @FXML
    TextField searchField;

    @FXML
    Button searchBtn;

    @FXML
    TableView<UserEntity> usersTbl;

    @FXML
    Button selectBtn;

    @FXML
    Button cancelBtn;

    ISearchUserViewModel viewModel;

    DialogScreenHandler dialogScreenHandler;

    @Inject
    public SearchUserScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, ISearchUserViewModel viewModel) {
        super(Config.SEARCH_USER_PATH, navigation);

        this.viewModel = viewModel;

        searchBtn.setOnMouseClicked(a -> {
            String searchTxt = searchField.getText();
            if(searchTxt.length() == 0) return;
            List<UserEntity> searchRes = viewModel.handleSearch(searchTxt, 20, 0);
            usersTbl.getItems().clear();
            usersTbl.getItems().addAll(searchRes);
        });
    }

    @Override
    public void show() {
        TableColumn<UserEntity, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UserEntity, TextField> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<UserEntity, TextField> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        usersTbl.getColumns().addAll(idCol, phoneCol, emailCol);
        super.show();
    }

    @Override
    public void onShow(){

    }

    public ISearchUserViewModel getViewModel(){
        return viewModel;
    }
}
