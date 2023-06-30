package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public MessageResponseDto createUser(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return userService.createUser(signUpRequestDto);
    }

    @PostMapping("/login")
    public MessageResponseDto loginUser(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse res) {
        return userService.loginUser(loginRequestDto, res);
    }

//    ResponseEntity

}
