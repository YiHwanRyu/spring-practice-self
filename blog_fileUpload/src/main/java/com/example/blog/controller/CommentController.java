package com.example.blog.controller;

import com.example.blog.auth.UserDetailsImpl;
import com.example.blog.dto.CommentRequestDto;
import com.example.blog.dto.CommentResponseDto;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public CommentResponseDto createComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CommentRequestDto requestDto){
        return commentService.createComment(userDetails.getUsername(), requestDto);
    }

    // 댓글 수정
    @PutMapping("/comments/{id}")
    public CommentResponseDto updateComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody CommentRequestDto requestDto){
        return commentService.updateComment(userDetails.getUser().getRole().toString(), userDetails.getUsername(), id, requestDto);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<MessageResponseDto> deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id){
        return commentService.deleteComment(userDetails.getUser().getRole().toString(), userDetails.getUsername(), id);
    }
}

