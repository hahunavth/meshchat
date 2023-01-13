package com.meshchat.client.controllers;

import com.meshchat.client.ModelSingleton;
import com.meshchat.client.model.*;
import com.meshchat.client.views.home.MessageScreenHandler;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.MapChangeListener;

public class MessageController extends BaseController<MessageScreenHandler> {
    public enum Type {
        CONV,
        CHAT
    }
    private Type type;
    private final SimpleLongProperty room_id = new SimpleLongProperty();    // chat id or conv id
    private MapChangeListener<Long, Message> newMsgListener;

    @Override
    public void setScreenHandler(MessageScreenHandler screenHandler) {
        super.setScreenHandler(screenHandler);

        Long uid = ModelSingleton.getInstance().dataSource.getUserProfile().getEntity().getId();
        ChatGen room;
        System.out.println(ModelSingleton.getInstance().dataSource.oChatMap);
        System.out.println(room_id.get());
        // add msg list
        if (type == Type.CHAT) room = ModelSingleton.getInstance().dataSource.oChatMap.get(room_id.get());
        else room = ModelSingleton.getInstance().dataSource.oConvMap.get(room_id.get());
        if (room != null) {
            room.getOMsgMap().forEach((id, msg) -> {
                this.getScreenHandler().addMsg(msg.getEntity().getContent(), msg.getEntity().getFrom_user_id(), uid, id);
            });

            // add msg list listener
            newMsgListener = (MapChangeListener<Long, Message>) change -> {
                // handle add new message here
                if (change.wasAdded()) {
                    this.getScreenHandler().addMsg(change.getValueAdded().getEntity().getContent(), change.getValueAdded().getEntity().getFrom_user_id(), uid, change.getKey());
                } else if (change.wasRemoved()) {
                    this.getScreenHandler().disableMsg(change.getKey());
                }
            };
            room.getOMsgMap().addListener(newMsgListener);
        } else {
            this.getScreenHandler().setName("");
        }

        // remove msg listener when choose new room
        room_id.addListener((observable, oldValue, newValue) -> {
            // remove old listener
            removeListenerFromChatOrConv();
            // add new
            ChatGen room1 = null;
            if (type == Type.CHAT) room1 = ModelSingleton.getInstance().dataSource.oChatMap.get(room_id.get());
            else room1 = ModelSingleton.getInstance().dataSource.oConvMap.get(room_id.get());
            if (room1 != null) {

                // set name
                if (room1 instanceof Conv) {
                    this.getScreenHandler().setName(((Conv) room1).getName());
                } else {
                    this.getScreenHandler().setName(((Chat) room1).getUser2().getUsername());
                }

                newMsgListener = change -> {
                    // handle add new message here
                    if (change.wasAdded()) {
                        this.getScreenHandler().addMsg(change.getValueAdded().getEntity().getContent(), change.getValueAdded().getEntity().getFrom_user_id(), uid, change.getKey());
                    } else if (change.wasRemoved()) {
                        this.getScreenHandler().disableMsg(change.getKey());
                    }
                };
                room1.getOMsgMap().addListener(newMsgListener);
            }
        });

        // send message
        screenHandler.setOnSubmit(event -> {
            ModelSingleton.getInstance().tcpClient.send(screenHandler.getText());
            long from_uid = ModelSingleton.getInstance().dataSource.getUserProfile().getEntity().getId();
            String content = screenHandler.getText();
            Message message = new Message(
                    this.room_id.get(),
                    from_uid,
                    -1,
                    content,
                    100000000,
                    false
            );
            screenHandler.addMsg(content, from_uid, from_uid, 0L );
        });

    }

    public void removeListenerFromChatOrConv () {
        if (this.newMsgListener != null) {
            ChatGen room;
            // add msg list
            if (type == Type.CHAT) room = ModelSingleton.getInstance().dataSource.oChatMap.get(room_id.get());
            else room = ModelSingleton.getInstance().dataSource.oConvMap.get(room_id.get());
            room.getOMsgMap().removeListener(this.newMsgListener);
        }
    }

    public void setRoomInfo (Type type, Long room_id) {
        this.type = type;
        this.room_id.set(room_id);
    }

    public Type getType() {
        return type;
    }
}
