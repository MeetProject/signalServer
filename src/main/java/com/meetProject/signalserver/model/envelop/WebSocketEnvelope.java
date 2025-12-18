package com.meetProject.signalserver.model.envelop;

public interface WebSocketEnvelope<T> {
    String getType();
    String getPath();
    T getPayload();
}
