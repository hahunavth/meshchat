package com.meshchat.client.views.factories;

import com.meshchat.client.Launcher;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.model.Message;
import com.meshchat.client.model.UserProfile;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.net.client.TCPBasedClient;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.views.components.MsgItem;
import com.meshchat.client.views.components.MsgItemSend;
import com.meshchat.client.views.components.MsgItemRecv;

public class MsgItemComponentFactory {
    public MsgItem getItem(ChatRoomType type, Message message, Long uid) {
        if(uid.equals(message.getEntity().getFrom_user_id()))
            return (new MsgItemSend(message));
        else
        {
            // NOTE: anti pattern
            TCPNativeClient client = Launcher.injector.getInstance(TCPNativeClient.class);
            UserEntity ue = client._get_user_by_id(message.getEntity().getFrom_user_id());
            return (new MsgItemRecv(message, ue.getUsername()));
        }
//            if (type == ChatRoomType.CHAT)
//            return (new MsgItemRecv(message));
//        else if (type == ChatRoomType.CONV)
//        else
//            return null;
    }

}
