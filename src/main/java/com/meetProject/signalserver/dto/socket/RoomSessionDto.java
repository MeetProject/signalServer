package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.dto.common.AppData;
import com.meetProject.signalserver.dto.common.ConsumerParams;
import com.meetProject.signalserver.dto.common.MediaOption;
import com.meetProject.signalserver.dto.common.Participant;
import com.meetProject.signalserver.dto.common.RtpCapabilities;
import com.meetProject.signalserver.dto.common.RtpParameters;
import com.meetProject.signalserver.dto.common.SignalResponse;
import com.meetProject.signalserver.dto.common.TransportOptions;
import com.meetProject.signalserver.model.dto.common.*;
import com.meetProject.signalserver.dto.common.TransportOptions.DtlsParameters;
import java.util.List;

public class RoomSessionDto {
    public record UserCapabilityPayload(String correlationId) {}
    public record UserCapabilityResponse(String correlationId, RtpCapabilities capabilities) implements SignalResponse {}

    public record UserDtlsPayload(String correlationId, DtlsDirection direction) {}
    public record UserDtlsResponse(String correlationId, TransportOptions options) implements SignalResponse {}

    public record UserDtlsConnectPayload(String correlationId, DtlsParameters dtlsParameters, String direction) {}
    public record UserDtlsConnectResponse(String correlationId) implements SignalResponse {}

    public record UserRtlsPayload(String correlationId, AppData appData, String kind, RtpParameters rtpParameters) {}
    public record UserRtlsResponse(String correlationId, String producerId) implements SignalResponse {}

    public record UserConsumerParamsPayload(String correlationId, String targetId, String producerId, RtpCapabilities rtpCapabilities) {}
    public record UserConsumerParamsResponse(String correlationId, ConsumerParams consumerParams) implements SignalResponse {}

    public record UserConsumerResumePayload(String consumerId) {}

    public record UserConsumerPausePayload(String consumerId) {}

    public record UserProducerMutePayload(String correlationId, String producerId) {}
    public record UserProducerMuteResponse(String correlationId) implements SignalResponse {}

    public record JoinPayload(String roomId, String correlationId, MediaOption mediaOption) {}
    public record JoinResponse(String correlationId, List<Participant> participants) implements SignalResponse {}

    public record ResyncPayload(String roomId, String correlationId) {}
    public record ResyncResponse(String correlationId, List<Participant> participants, boolean rejoinRequired) implements SignalResponse {}
}
