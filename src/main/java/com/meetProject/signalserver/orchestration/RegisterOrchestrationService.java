package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class RegisterOrchestrationService {
    private final UserService userService;

    public RegisterOrchestrationService(UserService userService) {
        this.userService = userService;
    }

    public RegisterResponse registerUser(String userId, String userName, String userProfileColor) {
        if(userService.getUser(userId) != null){
            throw new IllegalArgumentException("user already exists");
        }
        User user = new User(userName, userProfileColor, userId, null);
        userService.addUser(userId, user);
        return new RegisterResponse(SignalType.REGISTER, userId);
    }
}
