package com.meetProject.signalserver.model.dto.common;

import java.util.Map;

import java.util.List;

public class RtpCapabilitiesDto {
    private List<RtpCodecCapabilityDto> codecs;
    private List<RtpHeaderExtensionDto> headerExtensions;

    public static class RtpCodecCapabilityDto {
        private String kind;
        private String mimeType;
        private Integer preferredPayloadType;
        private Integer clockRate;
        private Integer channels;
        private Map<String, Object> parameters;
        private List<RtcpFeedbackDto> rtcpFeedback;
    }

    public static class RtcpFeedbackDto {
        private String type;
        private String parameter;
    }

    public static class RtpHeaderExtensionDto {
        private String kind;
        private String uri;
        private Integer preferredId;
        private Boolean preferredEncrypt;
        private String direction;
    }
}