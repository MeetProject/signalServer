package com.meetProject.signalserver.model.dto.common;

import java.util.Map;

import java.util.List;

public class RtpCapabilities {
    private List<RtpCodecCapability> codecs;
    private List<RtpHeaderExtension> headerExtensions;

    public static class RtpCodecCapability {
        private String kind;
        private String mimeType;
        private Integer preferredPayloadType;
        private Integer clockRate;
        private Integer channels;
        private Map<String, Object> parameters;
        private List<RtcpFeedback> rtcpFeedback;
    }

    public static class RtcpFeedback {
        private String type;
        private String parameter;
    }

    public static class RtpHeaderExtension {
        private String kind;
        private String uri;
        private Integer preferredId;
        private Boolean preferredEncrypt;
        private String direction;
    }
}