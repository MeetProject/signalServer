package com.meetProject.signalserver.model.socket.signal;

import com.meetProject.signalserver.model.TrackInfo;
import java.util.Map;

public record AnswerPayload(String userId, String sdp, Map<String, TrackInfo> trackInfo) {}
