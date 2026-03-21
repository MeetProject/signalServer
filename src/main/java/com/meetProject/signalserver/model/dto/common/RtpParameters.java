package com.meetProject.signalserver.model.dto.common;

import java.util.List;
import java.util.Map;

public record RtpParameters(
        String mid,
        List<Codec> codecs,
        List<Encoding> encodings,
        List<HeaderExtension> headerExtensions,
        Rtcp rtcp
) {
    public record Codec(
            String mimeType,
            Integer payloadType,
            Integer clockRate,
            Integer channels,
            Map<String, Object> parameters,
            List<RtcpFeedback> rtcpFeedback
    ) {}

    public record RtcpFeedback(
            String type,
            String parameter
    ) {}

    public record Encoding(
            String rid,
            Long ssrc,
            Integer payloadType,
            Boolean active,
            Integer maxBitrate,
            Double scaleResolutionDownBy,
            String scalabilityMode,
            Boolean dtx
    ) {}

    public record HeaderExtension(
            String uri,
            Integer id,
            Boolean encrypt,
            Map<String, Object> parameters
    ) {}

    public record Rtcp(
            String cname,
            Boolean reducedSize,
            Boolean mux
    ) {}
}