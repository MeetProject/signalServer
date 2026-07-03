package com.meetProject.signalserver.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Participant {
    private final User user;
    private boolean isHandsUp;
    private MediaOption mediaOption;
    private final Set<String> producerIds = ConcurrentHashMap.newKeySet();

    private Participant(User user, boolean isHandsUp, MediaOption mediaOption) {
        this.user = user;
        this.isHandsUp = isHandsUp;
        this.mediaOption = mediaOption;
    }

    public static Participant join(User user, MediaOption mediaOption) {
        return new Participant(user, false, mediaOption);
    }

    public void addProducerId(String producerId) {
        producerIds.add(producerId);
    }

    public void removeProducerId(String producerId) {
        producerIds.remove(producerId);
    }

    public synchronized boolean toggleHandsUp() {
        this.isHandsUp = !isHandsUp;
        return isHandsUp;
    }

    public void updateMediaOption(MediaOption mediaOption) {
        this.mediaOption = mediaOption;
    }

    public User getUser() {
        return user;
    }

    public boolean isHandsUp() {
        return isHandsUp;
    }

    public MediaOption getMediaOption() {
        return mediaOption;
    }

    public List<String> getProducerIds() {
        return new ArrayList<>(producerIds);
    }

    public String getUserId() {
        return user.getId();
    }

    public String getUserName() {
        return user.getName();
    }
}
