package com.example.blog.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String message;
    private String statusCode;

    public LoginResponseDto(String message, String statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
