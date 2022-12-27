package com.meshchat.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversation
 */
public class Conv extends ChatGen implements ISchema{
    public long id;
    //
    public long admin_id;
    public User admin = new User();
    //
    public String name;

    public Map<Long, User> members = new HashMap<>();

    public void addMember(long id, User mem) {
        this.members.put(id, mem);
    }

    public void removeMember(long id) {
        this.members.remove(id);
    }

}
