package com.meshchat.client.views.form;

import com.google.inject.Inject;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Conv;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.ConvInfoViewModel;
import com.meshchat.client.viewmodels.interfaces.IConvInfoViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConvInfoScreenHandler extends BaseScreenHandler {

    @FXML
    private Text title;

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

    private IConvInfoViewModel viewModel;

    @Inject
    public ConvInfoScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, IConvInfoViewModel viewModel) {
        super(Config.CONV_INFO_PATH, navigation);

        this.viewModel = viewModel;

        this.title.setText("Conversation info");
        this.gname.setDisable(true);
        this.cancelBtn.setOnAction(this::goBack);
        this.createBtn.setText("Ok");

        TableColumn<UserEntity, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UserEntity, TextField> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<UserEntity, TextField> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<UserEntity, TextField> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
//        TableColumn<UserEntity, TextField> removeCol = new TableColumn<>();
        memberTbl.getColumns().addAll(idCol, nameCol, phoneCol, emailCol);
    }

    public IConvInfoViewModel getViewModel() {
        return viewModel;
    }

    public void goBack(Event e) {
        this.getNavigation().goBack().show();
    }

    @Override
    public void show(){
        super.show();
        this.onShow();
    }

    @Override
    public void onShow() {
        try {
            Conv conv = this.viewModel.getConvInfo();
            this.gname.setText(conv.getName().get());
            List<UserEntity> _memLs = new ArrayList<>();
            ObservableList<UserEntity> memLs = FXCollections.observableList(_memLs);
            conv.members.forEach((i, u) -> {
                memLs.add(u.getEntity());
            });
            memberTbl.setItems(memLs);
        } catch (APICallException e) {
            e.printStackTrace();
        }
    }
}
