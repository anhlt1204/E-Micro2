package com.edso.userservice.helper;

import com.edso.userservice.exception.InvalidException;
import com.edso.userservice.model.request.LoginRequest;
import com.edso.userservice.model.request.RegisterRequest;
import com.edso.userservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckValid {
    // Kiểm tra password có ít nhất 8 ký tự và chứa ít nhất 1 số và 1 chữ
    private static final String regexPassword = "^(?=.*\\d)(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";

    // Kiểm tra username có ít nhất 3 ký tự, có thể chứa - hoặc _
    private static final String regexUsername = "^[a-z0-9_-]{3,16}$";

    static Pattern pattern;
    static Matcher matcher;

    /**
     * Kiểm tra mật khẩu
     * @param password
     * @return
     */
    public static Boolean isPassword(String password) {
        pattern = Pattern.compile(regexPassword);
        matcher = pattern.matcher(password);
        return matcher.find();
    }

    /**
     * Kiểm tra username
     * @param username
     * @return
     */
    public static Boolean isUsername(String username) {
        pattern = Pattern.compile(regexUsername);
        matcher = pattern.matcher(username);
        return matcher.find();
    }

    public static void checkLoginRequest(LoginRequest request, UserRepository userRepository) {
        if (!isUsername(request.getUsername())) {
            throw new InvalidException("Invalid Username");
        }

        if (!isPassword(request.getPassword())) {
            throw new InvalidException("Password must be eight characters including one letter, one number character");
        }

        List<String> usernames = new ArrayList<>();
        userRepository.findAll().forEach(user -> usernames.add(user.getUsername()));

        int flag = 0;
        for (String u : usernames) {
            if (u.equals(request.getUsername().trim())) {
                flag = 1;
                break;
            }
        }

        if (flag == 0) {
            throw new InvalidException("Username does not exist");
        }
    }

    public static void checkRegisterRequest(RegisterRequest request, UserRepository userRepository) {
        if (!isUsername(request.getUsername())) {
            throw new InvalidException("Invalid Username");
        }

        if (!isPassword(request.getPassword())) {
            throw new InvalidException("Password must be eight characters including one letter, one number character");
        }

        List<String> usernames = new ArrayList<>();
        userRepository.findAll().forEach(user -> usernames.add(user.getUsername()));

        for (String u : usernames) {
            if (u.equals(request.getUsername().trim())) {
                throw new InvalidException("Username already exists");
            }
        }

//        if (!request.getRole().trim().equals("ADMIN")) {
//            if (!request.getRole().trim().equals("USER")) {
//                throw new InvalidException("Role must be is ADMIN or USER");
//            }
//        }
    }
}
