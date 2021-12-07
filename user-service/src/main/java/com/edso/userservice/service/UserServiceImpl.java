package com.edso.userservice.service;

import com.edso.userservice.exception.InvalidException;
import com.edso.userservice.helper.CheckValid;
import com.edso.userservice.model.entity.User;
import com.edso.userservice.model.request.LoginRequest;
import com.edso.userservice.model.request.RegisterRequest;
import com.edso.userservice.model.response.LoginResponse;
import com.edso.userservice.model.response.UserResponse;
import com.edso.userservice.repository.UserRepository;
import com.edso.userservice.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final RedisTemplate template;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    Base64.Encoder encoder = Base64.getEncoder();

    public UserServiceImpl(RedisTemplate template, UserRepository userRepository, JwtUtil jwtUtil) {
        this.template = template;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) throws Exception {

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new InvalidException("Invalid user");
        }

        String password = request.getPassword();
        if (!user.getPassword().equals(encoder.encodeToString(password.getBytes()))) {
            throw new InvalidException("Password incorrect ");
        }

        String username = user.getUsername();
        String token = jwtUtil.generateToken(username);

        template.opsForValue().set(token, username);
        System.out.println("Value of key token: " + template.opsForValue().get(token));

        return new LoginResponse(username, token);

    }

    @Override
    public UserResponse register(RegisterRequest request) {
        CheckValid.checkRegisterRequest(request, userRepository);
        UserResponse response = new UserResponse();
        User userSave = new User();
        userSave.setUsername(request.getUsername());
        userSave.setPassword(encoder.encodeToString(request.getPassword().getBytes()));
        userSave.setRole("USER");
        userRepository.save(userSave);
        response.setUsername(userSave.getUsername());
        return response;
    }

    @Override
    public List<UserResponse> listUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (User u : users) {
            UserResponse r = new UserResponse();
            r.setUsername(u.getUsername());
            responses.add(r);
        }
        return responses;
    }
}
