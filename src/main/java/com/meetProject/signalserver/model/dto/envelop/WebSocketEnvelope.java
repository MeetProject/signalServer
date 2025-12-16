package com.meetProject.signalserver.model.dto.envelop;

public interface WebSocketEnvelope<T> {
    String getType();
    String getPath();
    T getPayload();
}
