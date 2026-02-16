package com.meetProject.signalserver.model.dto.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Participant {
    private final User user;
    private boolean isHandUp;
    private MediaOption mediaOption;
    private final Set<String> producerIds;

    public Participant(User user, MediaOption mediaOption, List<String> producerId) {
        this.user = user;
        this.isHandUp = false;
        this.mediaOption = mediaOption;
        this.producerIds = new ConcurrentSkipListSet<>(producerId);
    }

    public void setMediaOption(MediaOption mediaOption) {
        this.mediaOption = mediaOption;
    }

    public void setHandUp(boolean value) {
        this.isHandUp = value;
    }

    public void addProducerId(String producerId) {
        this.producerIds.add(producerId);
    }

    public void removeProducerId(String producerId) {
        this.producerIds.remove(producerId);
    }

    public User getUser() {
        return this.user;
    }

    public boolean isHandUp() {
        return this.isHandUp;
    }

    public MediaOption getMediaOption() {
        return this.mediaOption;
    }

    public List<String> getProducerId() {
        return new ArrayList<>(producerIds);
    }
}
