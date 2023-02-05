package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.Conv;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.views.base.SelectMultipleUsersViewModel;

public interface IConvInfoViewModel extends SelectMultipleUsersViewModel {

    Conv getConvInfo() throws APICallException;
    void setConvId(long convId);

    void addMember(long user2Id) throws APICallException;
}
