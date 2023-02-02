package com.meshchat.client.viewmodels;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.db.entities.MsgEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.*;
import com.meshchat.client.net.client.ChatRoomType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;

import java.util.List;

public class MessageViewModel extends BaseViewModel {
    private ChatRoomType type;
    private final SimpleLongProperty room_id = new SimpleLongProperty();    // chat id or conv id
    private StringProperty name = new SimpleStringProperty();
    private final ObservableList<Message> msgListMap = FXCollections.observableArrayList();
    private MapChangeListener<Long, Message> newMsgListener;
    private EventHandler roomInfoHandler;

    public ChatGen getChatGen() {
        if(type==ChatRoomType.CONV)
            return this.dataStore.getOConvMap().get(room_id.get());
        else if(type == ChatRoomType.CHAT)
            return this.dataStore.getOChatMap().get(room_id.get());
        else
            throw new Error("Not implemented");
    }

    public void removeListenerFromChatOrConv () {
        if (this.newMsgListener != null) {
            ChatGen room;
            // add msg list
            if (type == ChatRoomType.CHAT) room = ModelSingleton.getInstance().dataStore.getOChatMap().get(room_id.get());
            else room = ModelSingleton.getInstance().dataStore.getOChatMap().get(room_id.get());
            room.getOMsgMap().removeListener(this.newMsgListener);
        }
    }

    /**
     * When select new conv or chat
     * @param type
     * @param room_id
     */
    public void setRoomInfo (ChatRoomType type, Long room_id) {
        this.type = type;
        this.room_id.set(room_id);
        if (this.getChatGen() != null) {
            // set room name
            this.name.set(this.getChatGen().getName().get());
            // add message
//            this.msgListMap.removeAll(this.msgListMap);
//            System.out.println(this.msgListMap.size());
//            this.getChatGen().getOMsgMap().forEach((id, msg) -> {
//                this.msgListMap.add(msg);
//            });
            // listen when msg list change
            this.getChatGen().getOMsgMap().addListener((MapChangeListener<? super Long, ? super Message>) (e) -> {
                if (e.wasAdded()) {
                    this.msgListMap.add(e.getValueAdded());
                }
                if (e.wasRemoved()) {
                    this.msgListMap.remove(e.getValueAdded());
                }
                System.out.println("event .............");
            });
        }

        if (this.roomInfoHandler != null) {
            System.out.println("Handle set room info");
            this.roomInfoHandler.handle(null);
        }
    }

    public void setRoomInfoHandler(EventHandler roomInfoHandler) {
        this.roomInfoHandler = roomInfoHandler;
    }

    public void fetchMsgList() throws APICallException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                msgListMap.removeAll(msgListMap);
                List<Long> msgIdList = ModelSingleton.getInstance().tcpClient._get_msg_all(type, room_id.intValue(), 10, 0);
                System.out.println(msgIdList);
                msgIdList.forEach(id -> {
                    MsgEntity msgEntity = null;
                    try {
                        msgEntity = ModelSingleton.getInstance().tcpClient._get_msg_detail(id);
                    } catch (APICallException e) {
                        throw new RuntimeException(e);
                    }
                    msgListMap.add(new Message(msgEntity));
                    // TODO: DO NOT USE DATA SOURCE FOR STORE MSG LIST
                    // dataStore.addMsg(msgEntity);
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public void sendMsg(String msg) throws Exception {
        if(room_id.get() != 0) {
            Message _msg = ModelSingleton.getInstance().tcpClient._send_msg(type, room_id.get(), 0, msg);
            msgListMap.add(_msg);
        }
    }

    public ObservableList<Message> getMsgList() {
        return this.msgListMap;
    }

    public ChatRoomType getType() {
        return type;
    }

    public LongProperty getRoomId() {
        return this.room_id;
    }

    public StringProperty getName() {
        return this.name;
    }
}
