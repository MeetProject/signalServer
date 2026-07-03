package com.meetProject.signalserver.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Participant {
    private User user;
    private boolean isHandsUp;
    private MediaOption mediaOption;
    private final Set<String> producerIds;

    public Participant(User user, boolean isHansUp, MediaOption mediaOption, List<String> producerIds) {
        this.user = user;
        this.isHandsUp = isHansUp;
        this.mediaOption = mediaOption;
        this.producerIds = new HashSet<>(producerIds);
    }

    public void addProducerId(String producerId) {
        producerIds.add(producerId);
    }

    public void removeProducerId(String producerId) {
        producerIds.remove(producerId);
    }

    public boolean toggleHandsUp() {
        boolean prev = isHandsUp;
        this.isHandsUp = !prev;
        return !prev;
    }

    public void updateMediaOption(MediaOption mediaOption) {
        this.mediaOption = mediaOption;
    }

    public User getUser() {
        return user;
    }

    public boolean getIsHandsUp() {
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

    public UserName getUserName() {
        return user.getUserName();
    }
}
