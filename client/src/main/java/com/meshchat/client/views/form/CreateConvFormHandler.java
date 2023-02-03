package com.meshchat.client.views.form;

import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.CreateConvViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import com.meshchat.client.views.search.SearchUserScreenHandler;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateConvFormHandler extends BaseScreenHandler implements Initializable {

    @FXML
    private TextField gname;

    @FXML
    private Button addBtn;

    @FXML
    private TableView<UserEntity> memberTbl;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    private CreateConvViewModel viewModel;

    private DialogScreenHandler dialogScreenHandler;

    public CreateConvFormHandler() {
        super(Config.CREATE_CONV_FORM_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new CreateConvViewModel();
        dialogScreenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
        createBtn.setOnAction((a)->{
            long conv_id = viewModel.handleCreate(gname.getText());
            if(conv_id == 0){
                dialogScreenHandler.getViewModel().setMessage("Cannot create conversation");
                dialogScreenHandler.show();
            }
        });

        cancelBtn.setOnAction((a)->{
            this.getNavigation().navigate(StackNavigation.WINDOW_LIST.HOME).show();
        });

        addBtn.setOnAction((a)->{
            SearchUserScreenHandler screenHandler = (SearchUserScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.SEARCH_USER);
            screenHandler.getViewModel().setSelectUserViewModel(this.viewModel);
        });
    }

    @Override
    public void show(){
        TableColumn<UserEntity, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UserEntity, TextField> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<UserEntity, TextField> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
//        TableColumn<UserEntity, TextField> removeCol = new TableColumn<>();
        memberTbl.getColumns().addAll(idCol, phoneCol, emailCol);
        memberTbl.setItems(FXCollections.observableList(viewModel.getSelectedUsers()));
    }

    @Override
    public void onShow() {
    }
}
