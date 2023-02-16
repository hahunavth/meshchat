package com.meshchat.client.viewmodels;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.meshchat.client.binding.IDataSource;
import com.meshchat.client.binding.ITCPService;
import com.meshchat.client.db.entities.MsgEntity;
import com.meshchat.client.exceptions.APICallException;
import com.meshchat.client.model.*;
import com.meshchat.client.net.client.ChatRoomType;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.interfaces.IMessageViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;

import java.util.List;

//@Singleton
public class MessageViewModel extends BaseViewModel implements IMessageViewModel {
    private ChatRoomType type;
    private final SimpleLongProperty room_id = new SimpleLongProperty();    // chat id or conv id
    private StringProperty name = new SimpleStringProperty();
    private final ObservableList<Message> msgListMap = FXCollections.observableArrayList();
    private MapChangeListener<Long, Message> newMsgListener;
    private EventHandler roomInfoHandler;


    @Inject
    public MessageViewModel(@IDataSource DataStore dataStore, @ITCPService TCPNativeClient client) {
        super(dataStore, client);

    }

    /**
     * Todo:
     */
    public void removeListenerFromChatOrConv () {
        if (this.newMsgListener != null) {
            ChatGen room;
            // add msg list
//            if (type == ChatRoomType.CHAT) room = this.getDataStore().getOChatMap().get(room_id.get());
//            else room = this.getDataStore().getOChatMap().get(room_id.get());
//            room.getOMsgMap().removeListener(this.newMsgListener);
        }
    }

    /**
     * When select new conv or chat
     * @param type
     * @param room_id
     */
    public void setRoomInfo (ChatRoomType type, Long room_id, ChatGen chatRoom) {

        this.type = type;
        this.room_id.set(room_id);
        this.name.set(chatRoom.getName().get());
        if (this.roomInfoHandler != null) {
            System.out.println("Handle set room info");
            this.roomInfoHandler.handle(null);
        }
    }

    public void setRoomInfoHandler(EventHandler roomInfoHandler) {
        this.roomInfoHandler = roomInfoHandler;
    }

    public void fetchMsgList(int limit, int offset) throws APICallException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                msgListMap.removeAll(msgListMap);
                List<Long> msgIdList = getTcpClient()._get_msg_all(type, room_id.intValue(), limit, offset);
                System.out.println(msgIdList);
                msgIdList.forEach(id -> {
                    MsgEntity msgEntity = null;
                    try {
                        msgEntity = getTcpClient()._get_msg_detail(id);
                    } catch (APICallException e) {
                        throw new RuntimeException(e);
                    }
                    msgListMap.add(new Message(msgEntity));
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * polling
     * @param limit
     * @param offset
     * @throws APICallException
     */
    @Override
    public void notifyMsgList(int limit, int offset) throws APICallException {
        List<Long> newLs = this.getTcpClient().notifyNewMsg();
//        List<Long> delLs = this.getTcpClient().notifyDeleteMsg(type, this.room_id.get());
        System.out.println("notify pooling: " + newLs);
        // remove notify flag
        newLs.forEach((id) -> {
            try {
                if (msgListMap.get(id.intValue()) == null) {
                    this.getTcpClient()._get_msg_detail(id);
                }
            } catch (APICallException e) {
                throw new RuntimeException(e);
            }
        });

        if(newLs.size() > 0)
            this.fetchMsgList(limit, offset);
    }

    public void sendMsg(String msg) throws Exception {
        if(room_id.get() != 0) {
            Message _msg = this.getTcpClient()._send_msg(type, room_id.get(), 0, msg);
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

    public long getCurrentUserId() {
        return this.getTcpClient().get_uid();
    }
}
