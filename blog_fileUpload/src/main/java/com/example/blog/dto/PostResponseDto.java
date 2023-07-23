package com.example.blog.dto;

import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private String title;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long id;
    private List<CommentResponseDto> commentList;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.author = post.getUsername();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.id = post.getId();
        this.commentList = post.getCommentList().stream().map(CommentResponseDto::new).toList();
    }
}
