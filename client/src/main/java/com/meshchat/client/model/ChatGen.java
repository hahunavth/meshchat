package com.meshchat.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Chung cho conv và chat
 */
public class ChatGen {
    public Map<Long, Message> msgMap = new HashMap<>();
    public ObservableMap<Long, Message> oMsgMap = FXCollections.observableMap(msgMap);

    public void addMessage(long id, Message msg) {
        this.msgMap.put(id, msg);
    }

    public void deleteMessage(long id) {
        Message msg = this.msgMap.get(id);
        if (msg != null) {
            msg.content = "";
            msg.isDeleted = true;
        }
    }
}
