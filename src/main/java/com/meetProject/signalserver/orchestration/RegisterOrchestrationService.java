package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.ErrorResponse;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class RegisterOrchestrationService {
    private final UserService userService;
    private final SignalMessagingService signalMessagingService;

    public RegisterOrchestrationService(UserService userService,  SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.signalMessagingService = signalMessagingService;
    }

    public void registerUser(String userId, String userName, String userProfileColor) {
        try {
            if(userService.getUser(userId) != null){
                throw new IllegalArgumentException("user already exists");
            }
            User user = new User(userName, userProfileColor, userId, null);
            userService.addUser(user);
            RegisterResponse response = new RegisterResponse(SignalType.REGISTER, userId);
            signalMessagingService.sendRegister(userId, response);
        } catch (Exception ex) {
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, ex.getMessage());
            signalMessagingService.sendError(userId, response);
        }

    }
}
