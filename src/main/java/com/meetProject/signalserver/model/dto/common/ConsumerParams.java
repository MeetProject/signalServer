package com.meetProject.signalserver.model.dto.common;

import java.util.Map;

public class ConsumerParams {
    private String id;
    private String producerId;
    private String kind;
    private RtpParameters rtpParameters;
    private Map<String, Object> appData;
}
