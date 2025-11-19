package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.RegisterPayload;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.RandomIdGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/user/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterPayload payload) {
        String userId =  RandomIdGenerator.uuidGenerator();
        User user = new User(userId, payload.userName(), payload.userColor(), null, false);
        userService.addUser(user);
        return ResponseEntity.ok(new RegisterResponse(userId));
    }
}
