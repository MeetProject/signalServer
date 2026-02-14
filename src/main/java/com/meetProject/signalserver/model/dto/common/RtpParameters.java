package com.meetProject.signalserver.model.dto.common;

import java.util.List;
import java.util.Map;

public class RtpParameters {
    private String mid;
    private List<Codec> codecs;
    private List<Encoding> encodings;
    private List<HeaderExtension> headerExtensions;
    private Rtcp rtcp;

    public static class Codec {
        private String mimeType;
        private Integer payloadType;
        private Integer clockRate;
        private Integer channels;
        private Map<String, Object> parameters;
        private List<RtcpFeedback> rtcpFeedback;
    }

    public static class RtcpFeedback {
        private String type;
        private String parameter;
    }

    public static class Encoding {
        private String rid;
        private Long ssrc;
        private Integer payloadType;
        private Boolean active;
        private Integer maxBitrate;
        private Double scaleResolutionDownBy;
        private String scalabilityMode;
        private Integer dtx;
    }

    public static class HeaderExtension {
        private String uri;
        private Integer id;
        private Boolean encrypt;
        private Boolean parameters;
    }

    public static class Rtcp {
        private String cname;
        private Boolean reducedSize;
        private Boolean mux;
    }
}
