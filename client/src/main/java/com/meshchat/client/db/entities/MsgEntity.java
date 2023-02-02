package com.meshchat.client.db.entities;

import com.meshchat.client.cnative.res.ResponseMsg;
import javafx.beans.property.*;

public class MsgEntity {
    private final LongProperty id;
    private final LongProperty from_user_id;
    private final LongProperty reply_to;
    private final StringProperty	 content;
    private final LongProperty created_at;
    private final LongProperty chat_id;
    private final LongProperty conv_id;
    private final IntegerProperty type;

    public MsgEntity() {
        id = new SimpleLongProperty();
        from_user_id = new SimpleLongProperty();
        reply_to = new SimpleLongProperty();
        created_at = new SimpleLongProperty();
        chat_id = new SimpleLongProperty();
        conv_id = new SimpleLongProperty();
        content = new SimpleStringProperty();
        type = new SimpleIntegerProperty();
    }

    public MsgEntity(ResponseMsg msg) {
        this.id = new SimpleLongProperty(msg.msg_id.intValue());
        this.from_user_id = new SimpleLongProperty(msg.from_uid.intValue());
        this.reply_to = new SimpleLongProperty(msg.reply_to.intValue());
        this.created_at = new SimpleLongProperty(msg.created_at.intValue());
        this.chat_id = new SimpleLongProperty(msg.chat_id.intValue());
        this.conv_id = new SimpleLongProperty(msg.conv_id.intValue());
        this.content = new SimpleStringProperty(msg.msg_content.get());
        this.type = new SimpleIntegerProperty(msg.msg_type.byteValue());
    }

    @Override
    public String toString() {
        return "MsgEntity {" +
                "id: " + this.id.get() + ", " +
                "from_user_id: " + this.from_user_id.get() + ", " +
                "reply_to: " + this.reply_to.get() + ", " +
                "created_at: " + this.created_at.get() + ", " +
                "chat_id: " + this.chat_id.get() + ", " +
                "conv_id: " + this.conv_id.get() + ", " +
                "content: " + this.content.get() + ", " +
                "type: " + this.type.get() + ", " +
                "}";
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public long getFrom_user_id() {
        return from_user_id.get();
    }

    public LongProperty from_user_idProperty() {
        return from_user_id;
    }

    public void setFrom_user_id(long from_user_id) {
        this.from_user_id.set(from_user_id);
    }

    public long getReply_to() {
        return reply_to.get();
    }

    public LongProperty reply_toProperty() {
        return reply_to;
    }

    public void setReply_to(long reply_to) {
        this.reply_to.set(reply_to);
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public long getCreated_at() {
        return created_at.get();
    }

    public LongProperty created_atProperty() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at.set(created_at);
    }

    public long getChat_id() {
        return chat_id.get();
    }

    public LongProperty chat_idProperty() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id.set(chat_id);
    }

    public long getConv_id() {
        return conv_id.get();
    }

    public LongProperty conv_idProperty() {
        return conv_id;
    }

    public void setConv_id(long conv_id) {
        this.conv_id.set(conv_id);
    }

    public int getType() {
        return type.get();
    }

    public IntegerProperty typeProperty() {
        return type;
    }

    public void setType(int type) {
        this.type.set(type);
    }
}
