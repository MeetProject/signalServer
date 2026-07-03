package com.meetProject.signalserver.constant;

public enum TopicType {
    LEAVE("leave"),
    CHAT("chat"),
    EMOJI("emoji"),
    DEVICE("device"),
    HANDUP("handup"),
    PARTICIPANT("participant"),
    RTLS("rtls"),
    PRODUCER_REMOVE("producer/remove"),
    ;

    private final String path;

    TopicType(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
