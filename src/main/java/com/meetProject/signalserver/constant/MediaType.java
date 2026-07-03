package com.meetProject.signalserver.constant;

public enum MediaType {
    CAPABILITIES("capabilities"),
    DTLS("dtls"),
    DTLSCONNECT("dtlsconnect"),
    RTLS("rtls"),
    PARAMS("params"),
    CONSUMER_RESUME("consumer/resume"),
    CONSUMER_PAUSE("consumer/pause"),
    PRODUCER_PAUSE("producer/pause"),
    PRODUCER_RESUME("producer/resume"),
    PRODUCER_REMOVE("producer/remove"),
    LEAVE("leave"),
    ;

    private final String path;

    MediaType(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
