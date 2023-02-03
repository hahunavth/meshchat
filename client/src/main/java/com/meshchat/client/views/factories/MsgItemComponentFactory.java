package com.meshchat.client.views.factories;

import com.meshchat.client.model.Message;
import com.meshchat.client.views.components.MsgItem;
import com.meshchat.client.views.components.MsgItemLeft;
import com.meshchat.client.views.components.MsgItemRight;

public class MsgItemComponentFactory {
    public MsgItem getItem(Message message, Long uid) {
        if(uid.equals(message.getEntity().getFrom_user_id()))
            return (new MsgItemLeft(message));
        else
            return (new MsgItemRight(message));
    }

}
