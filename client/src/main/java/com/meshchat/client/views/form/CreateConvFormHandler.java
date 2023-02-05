package com.meshchat.client.views.form;

import com.google.inject.Inject;
import com.meshchat.client.Launcher;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.CreateConvViewModel;
import com.meshchat.client.viewmodels.interfaces.ICreateConvViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.dialog.DialogScreenHandler;
import com.meshchat.client.views.navigation.StackNavigation;
import com.meshchat.client.views.search.SearchUserScreenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CreateConvFormHandler extends BaseScreenHandler {

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

    private ICreateConvViewModel viewModel;

    private DialogScreenHandler dialogScreenHandler;

    @Inject
    public CreateConvFormHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, ICreateConvViewModel viewModel) {
        super(Config.CREATE_CONV_FORM_PATH, navigation);

        TableColumn<UserEntity, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UserEntity, TextField> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<UserEntity, TextField> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<UserEntity, TextField> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberTbl.getColumns().addAll(idCol, nameCol, phoneCol, emailCol);
        memberTbl.setItems(viewModel.getSelectedUsers());

        this.viewModel = viewModel;

        dialogScreenHandler = (DialogScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.DIALOG);
        ICreateConvViewModel finalViewModel = viewModel;
        createBtn.setOnAction((a)->{
            long conv_id = finalViewModel.handleCreate(gname.getText());
            if(conv_id == 0){
                dialogScreenHandler.getViewModel().setMessage("Cannot create conversation");
                dialogScreenHandler.show();
            }
        });

        viewModel = Launcher.injector.getInstance(CreateConvViewModel.class);

        cancelBtn.setOnAction((a)->{
            this.getNavigation().goBack().show();
        });

        addBtn.setOnAction((a)->{
            // createConvFormHandler to SearchConvUser
            SearchUserScreenHandler screenHandler = (SearchUserScreenHandler) this.getNavigation().navigate(StackNavigation.WINDOW_LIST.SEARCH_CONV_USER);
            screenHandler.getViewModel().setSelectUserViewModel(this.viewModel);
            screenHandler.show();
        });
    }

    @Override
    public void show(){
        super.show();
    }

    @Override
    public void onShow() {
    }
}
