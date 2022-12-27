package com.meshchat.client.model;

public class Message implements ISchema{
    public long id;
    public long from_user_id;
    public long reply_to;
    public String content;
    public long created_at;
    public boolean isDeleted;

    public Message(long id, long from_user_id, long reply_to, String content, long created_at, boolean isDeleted) {
        this.id = id;
        this.from_user_id = from_user_id;
        this.reply_to = reply_to;
        this.content = content;
        this.created_at = created_at;
        this.isDeleted = isDeleted;
    }

    //    public long chat_id;
//    public long conv_id;
//    int	type;
}
