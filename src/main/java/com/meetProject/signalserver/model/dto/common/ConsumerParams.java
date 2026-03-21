package com.meetProject.signalserver.model.dto.common;

import java.util.Map;

public record ConsumerParams(String id, String producerId, String kind, RtpParameters rtpParameters, Map<String, Object> appData) {}
