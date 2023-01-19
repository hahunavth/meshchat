package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.Conv;
import javafx.util.Pair;

public class ConvViewModel extends BaseViewModel{
    public void handleCreateConv(String gname){
        if(ModelSingleton.getInstance().tcpClient._create_conv(gname)){
            /* Display success dialog */

        }else{
            /* Display failure dialog */

        }
    }

    public void handleDropConv(long id){
        ModelSingleton.getInstance().tcpClient._quit_conv(id);
    }
}
