package com.meetProject.signalserver.controller.socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.dto.application.DeviceResult;
import com.meetProject.signalserver.dto.application.HandsUpResult;
import com.meetProject.signalserver.dto.application.ProducerResult;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerPauseRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerResumeRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerRemoveRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.HandsUpResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ProducerRemoveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.UserProducerRemoveRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerPauseRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerResumeRequest;
import com.meetProject.signalserver.dto.socket.TopicResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.ParticipantService;
import com.meetProject.signalserver.service.RoomService;
import java.security.Principal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoomInteractionControllerTest {
    @Mock ParticipantService participantService;
    @Mock RoomService roomService;
    @Mock StompMessageSender sender;
    @InjectMocks RoomInteractionController controller;

    private final Principal principal = () -> "u1";

    @Test
    @DisplayName("채팅을 방에 브로드캐스트한다")
    void broadcastsChat() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");

        controller.sendChat(new ChatRequest("hi"), principal);

        ArgumentCaptor<TopicResponse> captor = ArgumentCaptor.forClass(TopicResponse.class);
        verify(sender).broadcast(org.mockito.ArgumentMatchers.eq("roomA"),
                org.mockito.ArgumentMatchers.eq(TopicType.CHAT), captor.capture());
        ChatResponse response = (ChatResponse) captor.getValue();
        assertThat(response.userId()).isEqualTo("u1");
        assertThat(response.message()).isEqualTo("hi");
    }

    @Test
    @DisplayName("이모지를 방에 브로드캐스트한다")
    void broadcastsEmoji() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");

        controller.sendEmoji(new EmojiRequest(Emoji.CLAP), principal);

        ArgumentCaptor<TopicResponse> captor = ArgumentCaptor.forClass(TopicResponse.class);
        verify(sender).broadcast(org.mockito.ArgumentMatchers.eq("roomA"),
                org.mockito.ArgumentMatchers.eq(TopicType.EMOJI), captor.capture());
        EmojiResponse response = (EmojiResponse) captor.getValue();
        assertThat(response.userId()).isEqualTo("u1");
        assertThat(response.emoji()).isEqualTo(Emoji.CLAP);
    }

    @Test
    @DisplayName("디바이스 상태 갱신 후 방에 브로드캐스트한다")
    void broadcastsDevice() {
        MediaOption mediaOption = new MediaOption(false, true);
        given(participantService.updateDevice("u1", mediaOption))
                .willReturn(new DeviceResult("roomA", "u1", mediaOption));

        controller.sendDevice(new DeviceRequest(mediaOption), principal);

        verify(sender).broadcast("roomA", TopicType.DEVICE, new DeviceResponse("u1", mediaOption));
    }

    @Test
    @DisplayName("프로듀서 제거는 미디어서버 통보와 방 브로드캐스트를 한다")
    void removesProducer() {
        given(participantService.removeProducer("u1", "p1"))
                .willReturn(new ProducerResult("roomA", "u1", "p1"));

        controller.removeTrack(new UserProducerRemoveRequest("p1", "video"), principal);

        verify(sender).sendToMediaServer(MediaType.PRODUCER_REMOVE, new ProducerRemoveRequest("u1", "p1"));
        verify(sender).broadcast("roomA", TopicType.PRODUCER_REMOVE, new ProducerRemoveResponse("u1", "video"));
    }

    @Test
    @DisplayName("consumer resume를 미디어서버로 릴레이한다")
    void relaysConsumerResume() {
        controller.consumerResume(new UserConsumerResumeRequest("c1"));

        verify(sender).sendToMediaServer(MediaType.CONSUMER_RESUME, new ConsumerResumeRequest("c1"));
    }

    @Test
    @DisplayName("consumer pause를 미디어서버로 릴레이한다")
    void relaysConsumerPause() {
        controller.consumerPause(new UserConsumerPauseRequest("c1"));

        verify(sender).sendToMediaServer(MediaType.CONSUMER_PAUSE, new ConsumerPauseRequest("c1"));
    }

    @Test
    @DisplayName("손들기 토글 결과를 방에 브로드캐스트한다")
    void broadcastsHandsUp() {
        given(participantService.toggleHandsUp("u1")).willReturn(new HandsUpResult("roomA", "u1", true));

        controller.sendHandUp(principal);

        verify(sender).broadcast("roomA", TopicType.HANDUP, new HandsUpResponse("u1", true));
    }

    @Test
    @DisplayName("퇴장 시 방 브로드캐스트와 미디어서버 통보를 한다")
    void broadcastsLeave() {
        given(roomService.leave("u1")).willReturn(Optional.of("roomA"));

        controller.sendLeave(principal);

        verify(sender).broadcast("roomA", TopicType.LEAVE, new LeaveResponse("u1"));
        verify(sender).sendToMediaServer(MediaType.LEAVE, new MediaLeaveRequest("roomA", "u1"));
    }

    @Test
    @DisplayName("참여 중인 방이 없으면 퇴장 알림을 보내지 않는다")
    void skipsLeaveWhenNotJoined() {
        given(roomService.leave("u1")).willReturn(Optional.empty());

        controller.sendLeave(principal);

        verifyNoInteractions(sender);
    }
}
