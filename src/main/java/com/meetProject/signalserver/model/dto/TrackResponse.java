package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.TrackInfo;
import java.util.Map;

public record TrackResponse(String userId, String roomId, Map<String,TrackInfo> track) implements SignalResponse {
    private final static SignalType TYPE = SignalType.TRACK;

    @Override
    public SignalType getSignalType() {
        return TYPE;
    }
}
