package com.meshchat.client.model;

import com.meshchat.client.db.entities.UserEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.concurrent.Flow;

/**
 * Reactive programming:
 * @see com.meshchat.client.net.providers.ApiProvider
 * @see TCPSimpleClient
 *
 * @see <a href="https://blog.avenuecode.com/reactive-streams-and-microservices-a-case-study">reactive-streams</a>
 * @see <a href="https://dzone.com/articles/reactive-streams-in-java-9">reactive-streams</a>
 *
 * <br>
 * <s>Observable pattern:</s>
 */
public class DataStore {
    // data
    private final UserProfile userProfile = new UserProfile();
    private final Map<Long, Conv> convMap = new HashMap<>();
    private final Map<Long, Chat> chatMap = new HashMap<>();

    // observable
    public final ObservableMap<Long, Conv> oConvMap = FXCollections.observableMap(convMap);
    public final ObservableMap<Long, Chat> oChatMap = FXCollections.observableMap(chatMap);

    // cache
//    public final Map<Long, >

    public DataStore() {
        /**
         * Fake data
         */
        /*
        this.getUserProfile().getEntity().setId(10);
        this.getUserProfile().getEntity().setEmail("a@b.c");
        this.getUserProfile().getEntity().setUsername("uname");
        this.getUserProfile().getEntity().setPassword("pwd");
        this.getUserProfile().getEntity().setPhone_number("0987654321");
        //  user
        UserEntity user1 = new UserEntity(20, "afdsaf", "fjfjfjfjf", "fjfjfjfjfjfjfjf");
        UserEntity user2 = new UserEntity(23, "User 2",  "dfjksdjkd", "fjfjfjfjfjfjfjf");
        // chat
        Chat chat1 = new Chat(user1);
        chat1.id = 100L;
        Chat chat2 = new Chat(user2);
        chat2.id = 120L;
        chat1.addMessage(3, 343, -1, "contentntntntn", 8765432, false);
        this.chatMap.put(
                chat1.id, chat1
        );
        this.chatMap.put(
                chat2.id, chat2
        );
         */
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Map<Long, Conv> getConvMap() {
        return convMap;
    }

    public void addConv(long id, Conv conv) {
        this.convMap.put(id, conv);
    }

    public Map<Long, Chat> getChatMap() {
        return chatMap;
    }

    public void addChat(long id, Chat chat) {
        this.oChatMap.put(id, chat);
    }
}
