package com.example.blog.controller;

import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PostMapping("/posts")
    public PostResponseDto createPost(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @RequestBody PostRequestDto requestDto) {
        return postService.createPost(tokenValue, requestDto);
    }

    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(tokenValue, id, requestDto);
    }

    @DeleteMapping("/posts/{id}")
    public MessageResponseDto deletePost(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id) {
        return postService.deletePost(tokenValue, id);
    }
}
