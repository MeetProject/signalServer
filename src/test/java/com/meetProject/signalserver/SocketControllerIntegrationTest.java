package com.meetProject.signalserver;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.controller.socket.MediaSessionController;
import com.meetProject.signalserver.controller.socket.RoomInteractionController;
import com.meetProject.signalserver.controller.socket.RoomSessionController;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.dto.common.AppData;
import com.meetProject.signalserver.dto.common.ConsumerParams;
import com.meetProject.signalserver.dto.common.RtpCapabilities;
import com.meetProject.signalserver.dto.common.RtpParameters;
import com.meetProject.signalserver.dto.common.TransportOptions;
import com.meetProject.signalserver.dto.common.TransportOptions.DtlsParameters;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerPauseRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerResumeRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerRemoveRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsResponse;
import com.meetProject.signalserver.dto.socket.ParticipantDto;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.HandsUpResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ParticipantResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ProducerRemoveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ProducerResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.UserProducerRemoveRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.ResyncRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.ResyncResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserCapabilityRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserCapabilityResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerParamsRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerPauseRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerResumeRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsConnectRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserProducerMuteRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserProducerMuteResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserRtlsRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserRtlsResponse;
import com.meetProject.signalserver.service.RoomService;
import com.meetProject.signalserver.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;

public class SocketControllerIntegrationTest extends IntegrationTestSupport {
    @Autowired
    RoomSessionController roomSessionController;
    @Autowired
    MediaSessionController mediaSessionController;
    @Autowired
    RoomInteractionController roomInteractionController;
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    @Qualifier("brokerChannel")
    AbstractSubscribableChannel brokerChannel;

    private final MediaOption mediaOption = new MediaOption(true, true);
    private final List<Message<?>> captured = Collections.synchronizedList(new ArrayList<>());

    private final ChannelInterceptor interceptor = new ChannelInterceptor() {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            captured.add(message);
            return message;
        }
    };

    @BeforeEach
    void setUp() {
        captured.clear();
        brokerChannel.addInterceptor(interceptor);
    }

    @AfterEach
    void tearDown() {
        brokerChannel.removeInterceptor(interceptor);
    }

    private Principal principal(String userId) {
        return () -> userId;
    }

    private record Ctx(String userId, String roomId) {}

    private Ctx joined() {
        String userId = userService.create("홍길동", "#ffffff");
        String roomId = roomService.save();
        roomSessionController.join(new JoinRequest(roomId, "cid-join", mediaOption), principal(userId));
        captured.clear();
        return new Ctx(userId, roomId);
    }

    private String userQueue(String userId) {
        return "/user/" + userId + "/queue/replies";
    }

    private String mediaDest(String path) {
        return "/user/mediaServer/media/" + path;
    }

    private String topic(String roomId, String path) {
        return "/topic/room/" + roomId + "/" + path;
    }

    private Message<?> find(String destination) {
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline) {
            synchronized (captured) {
                for (Message<?> message : captured) {
                    if (destination.equals(SimpMessageHeaderAccessor.getDestination(message.getHeaders()))) {
                        return message;
                    }
                }
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new AssertionError("목적지로 전송된 메시지가 없습니다: " + destination);
    }

    private <T> T payload(String destination, Class<T> type) throws Exception {
        Object body = find(destination).getPayload();
        if (body instanceof byte[] bytes) {
            return objectMapper.readValue(bytes, type);
        }
        return type.cast(body);
    }


    @Test
    @DisplayName("입장 시 요청자 응답과 참여자 브로드캐스트가 브로커로 나간다")
    void join() throws Exception {
        String userId = userService.create("홍길동", "#ffffff");
        String roomId = roomService.save();

        roomSessionController.join(new JoinRequest(roomId, "cid-1", mediaOption), principal(userId));

        JoinResponse reply = payload(userQueue(userId), JoinResponse.class);
        assertThat(reply.correlationId()).isEqualTo("cid-1");
        assertThat(reply.participants()).isEmpty();

        ParticipantResponse broadcast = payload(topic(roomId, "participant"), ParticipantResponse.class);
        assertThat(broadcast.participant().user().userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("resync 응답이 요청자에게 나간다")
    void resync() throws Exception {
        Ctx ctx = joined();

        roomSessionController.resync(new ResyncRequest(ctx.roomId(), "cid-r"), principal(ctx.userId()));

        ResyncResponse reply = payload(userQueue(ctx.userId()), ResyncResponse.class);
        assertThat(reply.correlationId()).isEqualTo("cid-r");
        assertThat(reply.rejoinRequired()).isFalse();
        assertThat(reply.participants()).hasSize(1);
    }

    @Test
    @DisplayName("capabilities를 미디어서버로 릴레이한다")
    void capabilities() throws Exception {
        Ctx ctx = joined();

        roomSessionController.capabilities(new UserCapabilityRequest("cid"), principal(ctx.userId()));

        assertThat(payload(mediaDest("capabilities"), CapabilitiesRequest.class))
                .isEqualTo(new CapabilitiesRequest("cid", ctx.userId(), ctx.roomId()));
    }

    @Test
    @DisplayName("dtls를 미디어서버로 릴레이한다")
    void dtls() throws Exception {
        Ctx ctx = joined();

        roomSessionController.dtls(new UserDtlsRequest("cid", DtlsDirection.send), principal(ctx.userId()));

        assertThat(payload(mediaDest("dtls"), DtlsRequest.class))
                .isEqualTo(new DtlsRequest("cid", ctx.userId(), ctx.roomId(), DtlsDirection.send));
    }

    @Test
    @DisplayName("dtls/connect를 미디어서버로 릴레이한다")
    void dtlsConnect() throws Exception {
        Ctx ctx = joined();
        DtlsParameters params = new DtlsParameters("client", List.of());

        roomSessionController.dtlsConnect(new UserDtlsConnectRequest("cid", params, "send"), principal(ctx.userId()));

        assertThat(payload(mediaDest("dtlsconnect"), DtlsConnectRequest.class))
                .isEqualTo(new DtlsConnectRequest("cid", ctx.userId(), ctx.roomId(), params, "send"));
    }

    @Test
    @DisplayName("rtls를 미디어서버로 릴레이한다")
    void rtls() throws Exception {
        Ctx ctx = joined();
        AppData appData = new AppData("audio", ctx.userId());
        RtpParameters rtpParameters = new RtpParameters(null, List.of(), List.of(), List.of(), null);

        roomSessionController.rtls(new UserRtlsRequest("cid", appData, "audio", rtpParameters), principal(ctx.userId()));

        assertThat(payload(mediaDest("rtls"), RtlsRequest.class))
                .isEqualTo(new RtlsRequest("cid", ctx.userId(), ctx.roomId(), appData, "audio", rtpParameters));
    }

    @Test
    @DisplayName("consumerParams를 미디어서버로 릴레이한다")
    void consumerParams() throws Exception {
        Ctx ctx = joined();
        RtpCapabilities caps = new RtpCapabilities(List.of(), List.of());

        roomSessionController.consumerParams(
                new UserConsumerParamsRequest("cid", "t1", "p1", caps), principal(ctx.userId()));

        assertThat(payload(mediaDest("params"), ConsumerParamsRequest.class))
                .isEqualTo(new ConsumerParamsRequest("cid", ctx.userId(), ctx.roomId(), "t1", "p1", caps));
    }

    @Test
    @DisplayName("producer pause/resume를 미디어서버로 릴레이한다")
    void producerPauseResume() throws Exception {
        Ctx ctx = joined();

        roomSessionController.producerPause(new UserProducerMuteRequest("cid", "p1"), principal(ctx.userId()));
        assertThat(payload(mediaDest("producer/pause"),
                com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteRequest.class).producerId())
                .isEqualTo("p1");

        roomSessionController.producerResume(new UserProducerMuteRequest("cid", "p1"), principal(ctx.userId()));
        assertThat(payload(mediaDest("producer/resume"),
                com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteRequest.class).userId())
                .isEqualTo(ctx.userId());
    }

    // --- MediaSessionController: 미디어서버 응답 → 사용자 ---

    @Test
    @DisplayName("capabilities 응답을 사용자에게 전달한다")
    void mediaCapabilities() throws Exception {
        RtpCapabilities caps = new RtpCapabilities(List.of(), List.of());

        mediaSessionController.capabilities(new CapabilitiesResponse("cid", "u-1", caps));

        assertThat(payload(userQueue("u-1"), UserCapabilityResponse.class))
                .isEqualTo(new UserCapabilityResponse("cid", caps));
    }

    @Test
    @DisplayName("dtls 응답을 사용자에게 전달한다")
    void mediaDtls() throws Exception {
        TransportOptions options = new TransportOptions(null, null, null, null, null, null, null, null, null, null);

        mediaSessionController.dtls(new DtlsResponse("cid", "u-1", options));

        assertThat(payload(userQueue("u-1"), UserDtlsResponse.class))
                .isEqualTo(new UserDtlsResponse("cid", options));
    }

    @Test
    @DisplayName("dtls/connect 응답을 사용자에게 전달한다")
    void mediaDtlsConnect() throws Exception {
        mediaSessionController.dtlsConnect(new DtlsConnectResponse("cid", "u-1"));

        assertThat(payload(userQueue("u-1"), UserDtlsConnectResponse.class))
                .isEqualTo(new UserDtlsConnectResponse("cid"));
    }

    @Test
    @DisplayName("rtls 응답은 프로듀서 등록 후 사용자 전달과 방 브로드캐스트를 한다")
    void mediaRtls() throws Exception {
        Ctx ctx = joined();

        mediaSessionController.rtls(new RtlsResponse("cid", ctx.userId(), "p1"));

        assertThat(payload(userQueue(ctx.userId()), UserRtlsResponse.class))
                .isEqualTo(new UserRtlsResponse("cid", "p1"));
        assertThat(payload(topic(ctx.roomId(), "rtls"), ProducerResponse.class))
                .isEqualTo(new ProducerResponse(ctx.userId(), "p1"));
    }

    @Test
    @DisplayName("consumerParams 응답을 사용자에게 전달한다")
    void mediaConsumerParams() throws Exception {
        ConsumerParams cp = new ConsumerParams("id", "p1", "audio", null, null);

        mediaSessionController.consumerParams(new ConsumerParamsResponse("cid", "u-1", cp));

        assertThat(payload(userQueue("u-1"), UserConsumerParamsResponse.class))
                .isEqualTo(new UserConsumerParamsResponse("cid", cp));
    }

    @Test
    @DisplayName("producer mute 응답을 사용자에게 전달한다")
    void mediaProducerMute() throws Exception {
        mediaSessionController.producerMute(new ProducerMuteResponse("cid", "u-1"));

        assertThat(payload(userQueue("u-1"), UserProducerMuteResponse.class))
                .isEqualTo(new UserProducerMuteResponse("cid"));
    }

    // --- RoomInteractionController: 방 상호작용 ---

    @Test
    @DisplayName("채팅을 방에 브로드캐스트한다")
    void chat() throws Exception {
        Ctx ctx = joined();

        roomInteractionController.sendChat(new ChatRequest("hello"), principal(ctx.userId()));

        ChatResponse chat = payload(topic(ctx.roomId(), "chat"), ChatResponse.class);
        assertThat(chat.userId()).isEqualTo(ctx.userId());
        assertThat(chat.message()).isEqualTo("hello");
    }

    @Test
    @DisplayName("이모지를 방에 브로드캐스트한다")
    void emoji() throws Exception {
        Ctx ctx = joined();

        roomInteractionController.sendEmoji(new EmojiRequest(Emoji.CLAP), principal(ctx.userId()));

        EmojiResponse emoji = payload(topic(ctx.roomId(), "emoji"), EmojiResponse.class);
        assertThat(emoji.userId()).isEqualTo(ctx.userId());
        assertThat(emoji.emoji()).isEqualTo(Emoji.CLAP);
    }

    @Test
    @DisplayName("디바이스 상태를 방에 브로드캐스트한다")
    void device() throws Exception {
        Ctx ctx = joined();
        MediaOption updated = new MediaOption(false, true);

        roomInteractionController.sendDevice(new DeviceRequest(updated), principal(ctx.userId()));

        assertThat(payload(topic(ctx.roomId(), "device"), DeviceResponse.class))
                .isEqualTo(new DeviceResponse(ctx.userId(), updated));
    }

    @Test
    @DisplayName("프로듀서 제거는 미디어서버 통보와 방 브로드캐스트를 한다")
    void removeTrack() throws Exception {
        Ctx ctx = joined();

        roomInteractionController.removeTrack(new UserProducerRemoveRequest("p1", "video"), principal(ctx.userId()));

        assertThat(payload(mediaDest("producer/remove"), ProducerRemoveRequest.class))
                .isEqualTo(new ProducerRemoveRequest(ctx.userId(), "p1"));
        assertThat(payload(topic(ctx.roomId(), "producer/remove"), ProducerRemoveResponse.class))
                .isEqualTo(new ProducerRemoveResponse(ctx.userId(), "video"));
    }

    @Test
    @DisplayName("consumer resume/pause를 미디어서버로 릴레이한다")
    void consumerResumePause() throws Exception {
        roomInteractionController.consumerResume(new UserConsumerResumeRequest("c1"));
        assertThat(payload(mediaDest("consumer/resume"), ConsumerResumeRequest.class))
                .isEqualTo(new ConsumerResumeRequest("c1"));

        roomInteractionController.consumerPause(new UserConsumerPauseRequest("c2"));
        assertThat(payload(mediaDest("consumer/pause"), ConsumerPauseRequest.class))
                .isEqualTo(new ConsumerPauseRequest("c2"));
    }

    @Test
    @DisplayName("손들기 토글 결과를 방에 브로드캐스트한다")
    void handUp() throws Exception {
        Ctx ctx = joined();

        roomInteractionController.sendHandUp(principal(ctx.userId()));

        assertThat(payload(topic(ctx.roomId(), "handup"), HandsUpResponse.class))
                .isEqualTo(new HandsUpResponse(ctx.userId(), true));
    }

    @Test
    @DisplayName("퇴장 시 방 브로드캐스트와 미디어서버 통보를 한다")
    void leave() throws Exception {
        Ctx ctx = joined();

        roomInteractionController.sendLeave(principal(ctx.userId()));

        assertThat(payload(topic(ctx.roomId(), "leave"), LeaveResponse.class))
                .isEqualTo(new LeaveResponse(ctx.userId()));
        assertThat(payload(mediaDest("leave"), MediaLeaveRequest.class))
                .isEqualTo(new MediaLeaveRequest(ctx.roomId(), ctx.userId()));
    }
}
