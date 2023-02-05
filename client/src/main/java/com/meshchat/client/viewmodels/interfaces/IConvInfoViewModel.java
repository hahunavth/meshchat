package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Conv;
import com.meshchat.client.model.UserProfile;

public interface IConvInfoViewModel {

    Conv getConvInfo() throws APICallException;
    void setConvId(long convId);
}
