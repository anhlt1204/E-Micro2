package com.edso.userservice.controller;

import com.edso.userservice.model.request.LoginRequest;
import com.edso.userservice.model.request.RegisterRequest;
import com.edso.userservice.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Đăng nhập
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/login")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("Đăng nhập")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) throws Exception {
        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * Đăng ký tài khoản
     *
     * @param user
     * @return
     */
    @PostMapping(value = "/register")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("Đăng ký tài khoản")
    public ResponseEntity<?> saveUser(@Valid @RequestBody RegisterRequest user) {
        return ResponseEntity.ok(userService.register(user));
    }

    /**
     * Danh sách users
     *
     * @return
     */
    @GetMapping
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("Danh sách users")
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

}
