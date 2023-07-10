package com.example.blog.controller;

import com.example.blog.dto.CommentRequestDto;
import com.example.blog.dto.CommentResponseDto;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    // 댓글 생성
    @PostMapping("/comments")
    public CommentResponseDto createComment(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @RequestBody CommentRequestDto requestDto){
        return commentService.createComment(tokenValue, requestDto);
    }

    // 댓글 수정
    @PutMapping("/comments/{id}")
    public CommentResponseDto updateComment(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id, @RequestBody CommentRequestDto requestDto){
        return commentService.updateComment(tokenValue, id, requestDto);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<MessageResponseDto> deleteComment(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id){
        return commentService.deleteComment(tokenValue, id);
    }
}

