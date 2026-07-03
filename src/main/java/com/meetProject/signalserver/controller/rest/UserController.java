package com.meetProject.signalserver.controller.rest;

import com.meetProject.signalserver.dto.rest.UserSessionDto.RegisterRequest;
import com.meetProject.signalserver.dto.rest.UserSessionDto.RegisterResponse;
import com.meetProject.signalserver.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest payload) {
        String userId = userService.create(payload.userName(), payload.userColor());
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(userId));
    }
}
