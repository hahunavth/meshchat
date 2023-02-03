package com.meshchat.client.views.search;

import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.SearchUserViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchUserScreenHandler extends BaseScreenHandler implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchBtn;

    @FXML
    private TableView<UserEntity> usersTbl;

    @FXML
    private Button selectBtn;

    @FXML
    private Button cancelBtn;

    private SearchUserViewModel viewModel;

    private DialogScreenHandler dialogScreenHandler;

    public SearchUserScreenHandler() {
        super(Config.SEARCH_USER_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new SearchUserViewModel();
        dialogScreenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
        searchBtn.setOnAction(a -> {
            String searchTxt = searchField.getText();
            if(searchTxt.length() == 0) return;
            ArrayList<UserEntity> searchRes = viewModel.handleSearch(searchTxt, 0, 20);
            usersTbl.getItems().clear();
            usersTbl.getItems().addAll(searchRes);
        });

        selectBtn.setOnAction(a -> {
            UserEntity selectedUser = usersTbl.getSelectionModel().getSelectedItem();
            viewModel.addSelectedUser(selectedUser);
            viewModel.clearSelectUserViewModel();
        });

        cancelBtn.setOnAction(a -> {
            viewModel.clearSelectUserViewModel();
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
    }

    @Override
    public void onShow(){

    }

    public SearchUserViewModel getViewModel(){
        return viewModel;
    }
}
