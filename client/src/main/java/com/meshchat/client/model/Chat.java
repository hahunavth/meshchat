package com.meshchat.client.model;

import java.util.ArrayList;
import java.util.List;

public class Chat extends ChatGen {
//    private long id;
//    public long member1;
//    public long member2;
    private User user2 = new User();

    public Chat(User user2) {
        this.user2 = user2;
    }

    public User getUser2() {
        return user2;
    }
}
