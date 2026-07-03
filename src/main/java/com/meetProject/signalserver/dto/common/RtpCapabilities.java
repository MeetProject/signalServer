package com.meetProject.signalserver.dto.common;

import java.util.Map;

import java.util.List;

public record RtpCapabilities(
        List<RtpCodecCapability> codecs,
        List<RtpHeaderExtension> headerExtensions
) {
    public record RtpCodecCapability(
            String kind,
            String mimeType,
            Integer preferredPayloadType,
            Integer clockRate,
            Integer channels,
            Map<String, Object> parameters,
            List<RtcpFeedback> rtcpFeedback
    ) {}

    public record RtcpFeedback(
            String type,
            String parameter
    ) {}

    public record RtpHeaderExtension(
            String kind,
            String uri,
            Integer preferredId,
            Boolean preferredEncrypt,
            String direction
    ) {}
}