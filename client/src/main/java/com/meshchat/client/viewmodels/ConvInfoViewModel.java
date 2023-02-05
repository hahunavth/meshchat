package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Conv;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IConvInfoViewModel;

public class ConvInfoViewModel extends BaseViewModel implements IConvInfoViewModel {

    private long convId;
    @Inject
    public ConvInfoViewModel(DataStore dataStore, TCPNativeClient client) {
        super(dataStore, client);
    }

    public void setConvId(long convId) {
        this.convId = convId;
    }

    @Override
    public Conv getConvInfo() throws APICallException {
        return this.getTcpClient()._get_conv_info(convId);
    }
}
