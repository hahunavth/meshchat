package com.meshchat.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.concurrent.Flow;

/**
 * Reactive programming:
 * @see com.meshchat.client.net.providers.ApiProvider
 * @see com.meshchat.client.net.TCPClient
 *
 * @see <a href="https://blog.avenuecode.com/reactive-streams-and-microservices-a-case-study">reactive-streams</a>
 * @see <a href="https://dzone.com/articles/reactive-streams-in-java-9">reactive-streams</a>
 *
 * <br>
 * <s>Observable pattern:</s>
 */
public class DataSource implements Flow.Subscriber<char[]> {
    // reactive streams
    private Flow.Subscription subscription;

    // data
    private final User user = new User();
    private final Map<Long, Conv> convMap = new HashMap<>();
    private final Map<Long, Chat> chatMap = new HashMap<>();

    // observable
    public final ObservableMap<Long, Conv> oConvMap = FXCollections.observableMap(convMap);
    public final ObservableMap<Long, Chat> oChatMap = FXCollections.observableMap(chatMap);

    public DataSource() {
        /**
         * Fake data
         */
        this.getUserProfile().setId(10);
        this.getUserProfile().setEmail("a@b.c");
        this.getUserProfile().setUsername("uname");
        this.getUserProfile().setPassword("pwd");
        this.getUserProfile().setPhone_number("0987654321");
        User user1 = new User(20, "afdsaf", "fjfjfjfjf", "dfjksdjkd", "fjfjfjfjfjfjfjf");
        User user2 = new User(23, "User 2", "fjfjfjfjf", "dfjksdjkd", "fjfjfjfjfjfjfjf");
        Chat chat1 = new Chat(user1);
        Chat chat2 = new Chat(user2);
        chat1.addMessage(3, new Message(343, 10, -1, "contentntntntn", 8765432, false));
        this.chatMap.put(
                100L, chat1
        );
        this.chatMap.put(
                120L, chat2
        );
    }

    public User getUserProfile() {
        return user;
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
        this.chatMap.put(id, chat);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println(subscription);
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(char[] item) {
        System.out.println("Consumer: onNext" + Arrays.toString(item));
        // receive list of item
        // send num of accepted items
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Consumer: onComplete");
    }
}
