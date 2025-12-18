package com.meetProject.signalserver.model.envelop;

import com.meetProject.signalserver.constant.TopicType;

public record TopicEnvelope<T>(TopicType path, T payload) implements WebSocketEnvelope<T> {

    @Override
    public String getType() {
        return "topic";
    }

    @Override
    public String getPath() {
        return path.name();
    }

    @Override
    public T getPayload() {
        return payload;
    }
}