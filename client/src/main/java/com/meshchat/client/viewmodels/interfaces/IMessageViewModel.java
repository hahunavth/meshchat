package com.meshchat.client.viewmodels.interfaces;

import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.ChatGen;
import com.meshchat.client.model.Message;
import com.meshchat.client.net.client.ChatRoomType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

public interface IMessageViewModel {

    void setRoomInfo(ChatRoomType type, Long room_id, ChatGen chatRoom);

    void setRoomInfoHandler(EventHandler roomInfoHandler);

    void fetchMsgList() throws APICallException;

    void sendMsg(String msg) throws Exception;

    ObservableList<Message> getMsgList();

    ChatRoomType getType();

    LongProperty getRoomId();

    StringProperty getName();

    long getCurrentUserId();
}
