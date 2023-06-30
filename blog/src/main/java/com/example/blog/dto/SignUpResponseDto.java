package com.example.blog.dto;

import lombok.Getter;

@Getter
public class SignUpResponseDto {
    private String message;
    private String statusCode;

    public SignUpResponseDto(String message, String statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
