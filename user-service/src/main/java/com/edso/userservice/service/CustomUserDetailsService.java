package com.edso.userservice.service;

import com.edso.userservice.model.entity.User;
import com.edso.userservice.model.request.RegisterRequest;
import com.edso.userservice.model.response.UserResponse;
import com.edso.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse save(RegisterRequest user) {
        UserResponse response = new UserResponse();
        User userSave = new com.edso.userservice.model.entity.User();
        userSave.setUsername(user.getUsername());
        userSave.setPassword("abcabc");
        userSave.setRole("USER");
        userRepository.save(userSave);
        response.setUsername(userSave.getUsername());
        return response;
    }

}
