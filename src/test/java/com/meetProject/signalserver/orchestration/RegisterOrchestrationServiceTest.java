package com.meetProject.signalserver.orchestration;

import static org.mockito.Mockito.*;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterOrchestrationServiceTest {

    private SignalMessagingService signalMessagingService;
    private RegisterOrchestrationService registerService;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService();
        signalMessagingService = mock(SignalMessagingService.class);
        registerService = new RegisterOrchestrationService(userService, signalMessagingService);
    }

    @Test
    @DisplayName("유저 등록 성공")
    void registerUserSuccessfully() {
        User newUser = new User("user", "#000000", "userId", null);
        registerService.registerUser(newUser);
        verify(signalMessagingService, times(1)).sendRegister(eq("userId"), any());
    }

    @Test
    @DisplayName("이미 존재하는 유저 등록 시도")
    void registerUserAlreadyExists() {
        User existingUser = new User("user", "#000000", "userId", null);

        registerService.registerUser(existingUser);
        registerService.registerUser(existingUser);

        verify(signalMessagingService, times(1)).sendError(eq("userId"), argThat(
                response -> response.message().equals(ErrorMessage.USER_ALREADY_EXISTS)
        ));
    }

}