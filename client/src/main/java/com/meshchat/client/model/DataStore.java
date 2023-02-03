package com.meshchat.client.model;

import com.google.inject.Singleton;
import com.meshchat.client.db.entities.MsgEntity;
import com.meshchat.client.db.entities.UserEntity;
import com.meshchat.client.net.client.ChatRoomType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;

/**
 * Reactive programming:
 *
 * @see <a href="https://blog.avenuecode.com/reactive-streams-and-microservices-a-case-study">reactive-streams</a>
 * @see <a href="https://dzone.com/articles/reactive-streams-in-java-9">reactive-streams</a>
 *
 * <br>
 * <s>Observable pattern:</s>
 */
@Singleton
public class DataStore {
    // data
    private final UserProfile userProfile = new UserProfile();
    private final Map<Long, Conv> convMap = new HashMap<>();
    private final Map<Long, Chat> chatMap = new HashMap<>();

    // observable
    private final ObservableMap<Long, Conv> oConvMap = FXCollections.observableMap(convMap);
    private final ObservableMap<Long, Chat> oChatMap = FXCollections.observableMap(chatMap);

    // cache
    private final Map<Long, UserProfile> userProfileMap = new HashMap<>();
    private final ObservableMap<Long, UserProfile> userProfileObservableMap = FXCollections.observableMap(userProfileMap);

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

    public ObservableMap<Long, Conv> getOConvMap() {
        return this.oConvMap;
    }

    public void addConv(long id, Conv conv) {
        this.oConvMap.put(id, conv);
    }

    public ObservableMap<Long, Chat> getOChatMap() {
        return this.oChatMap;
    }

    public void addChat(long id, Chat chat) {
        this.oChatMap.put(id, chat);
    }

    public ObservableMap<Long, UserProfile> getUserProfileCache() {
        return this.userProfileObservableMap;
    }

    public void addUserProfileCache(UserProfile userProfile) {
        this.userProfileObservableMap.put(userProfile.getEntity().getId(), userProfile);
    }

    public void resetMsgList() {

    }

    public void addMsg(MsgEntity msg) {
        System.out.println(msg);
        ChatGen room = null;
        if (msg.getChat_id() != 0) {
            room = this.getOChatMap().get(msg.getChat_id());
        } else if (msg.getConv_id() != 0) {
            room = this.getOConvMap().get(msg.getChat_id());
        } else {
            System.out.println("addMsg invalid type");
        }
        if (room != null)
            // FIXME: HARDCODE isDeleted
            room.addMessage(msg.getId(), msg.getFrom_user_id(), msg.getReply_to(), msg.getContent(), msg.getCreated_at(), false);
    }
}
