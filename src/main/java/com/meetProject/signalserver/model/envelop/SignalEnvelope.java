package com.meetProject.signalserver.model.envelop;

import com.meetProject.signalserver.constant.SignalType;

public record SignalEnvelope<T>(SignalType path, T payload) implements WebSocketEnvelope<T> {

    @Override
    public String getType() {
        return "signal";
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