package com.meshchat.client.model;

import com.meshchat.client.db.entities.MsgEntity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Message extends BaseModel {
    private final MsgEntity entity = new MsgEntity();
    private final BooleanProperty isDeleted = new SimpleBooleanProperty();

    public Message(long id, long from_user_id, long reply_to, String content, long created_at, boolean isDeleted) {
        this.entity.setId(id);
        this.entity.setFrom_user_id(from_user_id);
        this.entity.setReply_to(reply_to);
        this.entity.setContent(content);
        this.entity.setCreated_at(created_at);
        this.isDeleted.set(isDeleted);
    }

    public Message(MsgEntity entity) {
        this.entity.setId(entity.getId());
        this.entity.setType(entity.getType());
        this.entity.setConv_id(entity.getConv_id());
        this.entity.setChat_id(entity.getChat_id());
        this.entity.setContent(entity.getContent());
        this.entity.setCreated_at(entity.getCreated_at());
        this.entity.setReply_to(entity.getReply_to());
    }

    public MsgEntity getEntity() {
        return entity;
    }

    public boolean isIsDeleted() {
        return isDeleted.get();
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted.set(isDeleted);
    }
}
