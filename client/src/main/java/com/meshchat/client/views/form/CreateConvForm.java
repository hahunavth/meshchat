package com.meshchat.client.views.form;

import com.meshchat.client.utils.Config;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateConvForm extends BaseScreenHandler implements Initializable {

    @FXML
    private TextField gname;

    @FXML
    private Button addBtn;

    @FXML
    private TableView memberTbl;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    public CreateConvForm() {
        super(Config.CREATE_CONV_FORM_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void onShow() {

    }
}
