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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//@Singleton
public class MessageViewModel extends BaseViewModel implements IMessageViewModel {
    private ChatRoomType type;
    private final SimpleLongProperty room_id = new SimpleLongProperty();    // chat id or conv id
    private StringProperty name = new SimpleStringProperty();
    private final ObservableList<Message> msgListMap = FXCollections.observableArrayList();
    private MapChangeListener<Long, Message> newMsgListener;
    private EventHandler roomInfoHandler;
    private List<Long> lastedIds = new ArrayList<>();


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

        if(type == null) {
            System.out.println("Notify null");
            return;
        }

        List<Long> newLs = this.getTcpClient()._get_msg_all(this.type, this.room_id.intValue(), limit, offset);
        List<Long> oldLs = this.lastedIds;
        List<Long> newItems = new ArrayList<>();

        System.out.println("Notify ls: new=" + newLs + ", old=" + oldLs);

        if (oldLs.size() != 10) {
            oldLs.clear();
            oldLs.addAll(newLs);

            return ;
        }
        assert newLs.size() == 10;

        boolean closeFlag = false;
        for(int i = oldLs.size() - 1; i >= 0; i--) {
            for(int j = newLs.size() - 1; j >= 0; j--) {
                if(oldLs.get(i).equals(newLs.get(j))) {
                    closeFlag = true;
                } else {
                    newItems.add(newLs.get(j));
                }
                if(closeFlag) break;
            }
            if(closeFlag) break;
        }

        oldLs.clear();
        oldLs.addAll(newLs);

        newItems.forEach(id -> {
            MsgEntity msgEntity = null;
            try {
                msgEntity = getTcpClient()._get_msg_detail(id);
            } catch (APICallException e) {
                throw new RuntimeException(e);
            }
            if(msgEntity.getFrom_user_id() != getCurrentUserId())
                msgListMap.add(new Message(msgEntity));
        });

        System.out.println("Notify: " + newItems);
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
