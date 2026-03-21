package com.meetProject.signalserver.model.dto.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Participant {
    private final User user;
    @Setter
    private boolean isHandUp;
    @Setter
    private MediaOption mediaOption;
    private final Set<String> producerIds;

    public Participant(User user, MediaOption mediaOption, List<String> producerId) {
        this.user = user;
        this.isHandUp = false;
        this.mediaOption = mediaOption;
        this.producerIds = new ConcurrentSkipListSet<>(producerId);
    }

    public void addProducerId(String producerId) {
        this.producerIds.add(producerId);
    }

    public void removeProducerId(String producerId) {
        this.producerIds.remove(producerId);
    }
}
