package com.meetProject.signalserver.model.socket.signal;

import com.meetProject.signalserver.model.TrackInfo;
import java.util.Map;

public record AnswerResponse(String userId, String roomId, String sdp, Map<String, TrackInfo> trackInfo) {}
