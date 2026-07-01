package com.meetProject.signalserver.service;

import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.rest.UserSessionDto.RegisterResponse;
import com.meetProject.signalserver.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse create(String name, String profileColor) {
        User user = User.create(name, profileColor);
        userRepository.save(user);
        return new RegisterResponse(user.getId());
    }

    public void delete(String userId) {
        userRepository.deleteById(userId);
    }
}
