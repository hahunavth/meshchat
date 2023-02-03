package com.meshchat.client.views.dialog;

import com.google.inject.Inject;
import com.meshchat.client.utils.Config;
import com.meshchat.client.viewmodels.interfaces.IDialogViewModel;
import com.meshchat.client.views.base.BaseScreenHandler;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.navigation.StackNavigation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class DialogScreenHandler extends BaseScreenHandler {

    @FXML
    private Text message;
    @FXML
    private Button okBtn;

    private IDialogViewModel viewModel;
    @Inject
    public DialogScreenHandler(INavigation<StackNavigation.WINDOW_LIST> navigation, IDialogViewModel viewModel) {
        super(Config.DIALOG_PATH, navigation);

        this.viewModel = viewModel;
        this.message.textProperty().bindBidirectional(this.viewModel.getMessage());
        this.okBtn.setOnMouseClicked((e) -> {
            this.getNavigation().goBack().show();
        });
    }

    public IDialogViewModel getViewModel() {
        return this.viewModel;
    }

    @Override
    public void onShow() {

    }
}
