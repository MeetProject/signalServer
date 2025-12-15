package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.model.TrackInfo;
import java.util.Map;

public record TrackPayload(String userId, Map<String, TrackInfo> transceiver) {}
