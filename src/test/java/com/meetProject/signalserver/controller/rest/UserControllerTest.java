package com.meetProject.signalserver.controller.rest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.meetProject.signalserver.exception.InvalidInputException;
import com.meetProject.signalserver.exception.StompExceptionHandler;
import com.meetProject.signalserver.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = StompExceptionHandler.class))
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Test
    @DisplayName("등록 성공 시 201과 userId를 반환한다")
    void registersUser() throws Exception {
        given(userService.create("홍길동", "#ffffff")).willReturn("uid-1");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"홍길동\",\"userColor\":\"#ffffff\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("uid-1"));
    }

    @Test
    @DisplayName("이름이 비면 400과 INVALID_INPUT을 반환한다")
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"\",\"userColor\":\"#ffffff\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("사용자 이름은 필수입니다."));
    }

    @Test
    @DisplayName("색상이 비면 400을 반환한다")
    void rejectsBlankColor() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"홍길동\",\"userColor\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("색상 형식이 잘못되면 400을 반환한다")
    void rejectsInvalidColorFormat() throws Exception {
        given(userService.create("홍길동", "red"))
                .willThrow(new InvalidInputException("유효하지 않은 프로필 색상입니다."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"홍길동\",\"userColor\":\"red\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 프로필 색상입니다."));
    }

    @Test
    @DisplayName("예상치 못한 오류는 500과 INTERNAL_ERROR를 반환한다")
    void returnsInternalErrorOnUnexpected() throws Exception {
        given(userService.create("홍길동", "#ffffff")).willThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"홍길동\",\"userColor\":\"#ffffff\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }
}
