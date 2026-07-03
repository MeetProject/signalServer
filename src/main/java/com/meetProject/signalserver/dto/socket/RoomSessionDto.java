package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.common.AppData;
import com.meetProject.signalserver.dto.common.ConsumerParams;
import com.meetProject.signalserver.dto.common.RtpCapabilities;
import com.meetProject.signalserver.dto.common.RtpParameters;
import com.meetProject.signalserver.dto.common.SignalResponse;
import com.meetProject.signalserver.dto.common.TransportOptions;
import com.meetProject.signalserver.dto.common.TransportOptions.DtlsParameters;
import java.util.List;

public class RoomSessionDto {
    public record UserCapabilityRequest(String correlationId) {}
    public record UserCapabilityResponse(String correlationId, RtpCapabilities capabilities) implements SignalResponse {}

    public record UserDtlsRequest(String correlationId, DtlsDirection direction) {}
    public record UserDtlsResponse(String correlationId, TransportOptions options) implements SignalResponse {}

    public record UserDtlsConnectRequest(String correlationId, DtlsParameters dtlsParameters, String direction) {}
    public record UserDtlsConnectResponse(String correlationId) implements SignalResponse {}

    public record UserRtlsRequest(String correlationId, AppData appData, String kind, RtpParameters rtpParameters) {}
    public record UserRtlsResponse(String correlationId, String producerId) implements SignalResponse {}

    public record UserConsumerParamsRequest(String correlationId, String targetId, String producerId, RtpCapabilities rtpCapabilities) {}
    public record UserConsumerParamsResponse(String correlationId, ConsumerParams consumerParams) implements SignalResponse {}

    public record UserConsumerResumeRequest(String consumerId) {}

    public record UserConsumerPauseRequest(String consumerId) {}

    public record UserProducerMuteRequest(String correlationId, String producerId) {}
    public record UserProducerMuteResponse(String correlationId) implements SignalResponse {}

    public record JoinRequest(String roomId, String correlationId, MediaOption mediaOption) {
        public Participant to(User user) {
            return new Participant(user, false, mediaOption, List.of());
        }
    }
    public record JoinResponse(String correlationId, List<ParticipantDto> participants) implements SignalResponse {}

    public record ResyncRequest(String roomId, String correlationId) {}
    public record ResyncResponse(String correlationId, List<ParticipantDto> participants, boolean rejoinRequired) implements SignalResponse {}
}
