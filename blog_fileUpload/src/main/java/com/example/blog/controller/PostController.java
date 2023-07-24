package com.example.blog.controller;

import com.example.blog.auth.UserDetailsImpl;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public PostResponseDto createPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestPart("data") @Valid PostRequestDto requestDto,
                                      @RequestPart(value = "titleImg") MultipartFile titleImgMultiPartFile,
                                      @RequestPart(value = "subImg1", required = false) MultipartFile subImg1MultiPartFile,
                                      @RequestPart(value = "subImg2", required = false) MultipartFile subImg2MultiPartFile
                                      ) {
        return postService.createPost(userDetails.getUsername(), requestDto, titleImgMultiPartFile, subImg1MultiPartFile, subImg2MultiPartFile);
    }

    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(userDetails.getUser().getRole().toString(), userDetails.getUsername(), id, requestDto);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<MessageResponseDto> deletePost(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return postService.deletePost(userDetails.getUser().getRole().toString(), userDetails.getUsername(), id);
    }
}
