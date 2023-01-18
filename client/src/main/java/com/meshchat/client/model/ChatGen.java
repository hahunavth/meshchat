package com.meshchat.client.model;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Chung cho conv và chat
 */
public abstract class ChatGen extends BaseModel {
    private final Map<Long, Message> msgMap = new HashMap<>();
    private final ObservableMap<Long, Message> oMsgMap = FXCollections.observableMap(msgMap);

    public void addMessage(long id, long from_uid, long reply_to, String content, long created_at, boolean isDeleted) {
        this.msgMap.put(id, new Message(id, from_uid, reply_to, content, created_at, isDeleted));
    }

    public void deleteMessage(long id) {
        Message msg = this.msgMap.get(id);
        if (msg != null) {
            msg.getEntity().setContent("");
            msg.setIsDeleted(true);
        }
    }

    public ObservableMap<Long, Message> getOMsgMap() {
        return oMsgMap;
    }

    public abstract StringProperty getName();
}
