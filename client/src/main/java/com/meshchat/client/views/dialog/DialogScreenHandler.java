package com.meshchat.client.views.dialog;

import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.DialogViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogScreenHandler extends BaseScreenHandler implements Initializable {

    @FXML
    private Text message;
    @FXML
    private Button okBtn;

    private DialogViewModel viewModel;
    public DialogScreenHandler() {
        super(Config.DIALOG_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.viewModel = new DialogViewModel("");
        this.message.textProperty().bindBidirectional(this.viewModel.getMessage());
        this.okBtn.setOnMouseClicked((e) -> {
            this.getNavigation().goBack().show();
        });
    }

    public DialogViewModel getViewModel() {
        return this.viewModel;
    }
}
