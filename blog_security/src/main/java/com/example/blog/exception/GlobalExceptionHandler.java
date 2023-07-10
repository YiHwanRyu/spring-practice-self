package com.example.blog.exception;

import com.example.blog.dto.MessageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<MessageResponseDto> handleException(IllegalArgumentException ex) {
        MessageResponseDto restApiException = new MessageResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }

//    @ExceptionHandler({RuntimeException.class})
//    public ResponseEntity<MessageResponseDto> handleApiException(RuntimeException ex){
//        MessageResponseDto restApiException = new MessageResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.toString());
//        return new ResponseEntity<>(
//                // HTTP body
//                restApiException,
//                // HTTP status code
//                HttpStatus.BAD_REQUEST
//        );
//    }
}
