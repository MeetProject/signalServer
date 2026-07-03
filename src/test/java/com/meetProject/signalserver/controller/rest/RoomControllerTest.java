package com.meetProject.signalserver.controller.rest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.RoomService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RoomService roomService;

    @MockitoBean
    StompMessageSender stompMessageSender;

    @Test
    @DisplayName("방 생성 시 201과 roomId를 반환한다")
    void createsRoom() throws Exception {
        given(roomService.save()).willReturn("ROOM123456");

        mockMvc.perform(post("/api/rooms"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").value("ROOM123456"));
    }

    @Test
    @DisplayName("방 검증 결과를 반환한다")
    void validatesRoom() throws Exception {
        given(roomService.validate("roomA")).willReturn(true);

        mockMvc.perform(get("/api/rooms/roomA/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(true));
    }

    @Test
    @DisplayName("퇴장 시 방 브로드캐스트와 미디어서버 통보를 한다")
    void leavesRoom() throws Exception {
        given(roomService.leave("u1")).willReturn(Optional.of("roomA"));

        mockMvc.perform(post("/api/rooms/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\"}"))
                .andExpect(status().isOk());

        verify(stompMessageSender).broadcast("roomA", TopicType.LEAVE, new LeaveResponse("u1"));
        verify(stompMessageSender).sendToMediaServer(
                com.meetProject.signalserver.constant.MediaType.LEAVE, new MediaLeaveRequest("roomA", "u1"));
    }

    @Test
    @DisplayName("참여 중인 방이 없으면 알림을 보내지 않는다")
    void leavesWithoutRoom() throws Exception {
        given(roomService.leave("u1")).willReturn(Optional.empty());

        mockMvc.perform(post("/api/rooms/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u1\"}"))
                .andExpect(status().isOk());

        verifyNoInteractions(stompMessageSender);
    }

    @Test
    @DisplayName("userId가 비면 400을 반환한다")
    void rejectsBlankUserId() throws Exception {
        mockMvc.perform(post("/api/rooms/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }
}
