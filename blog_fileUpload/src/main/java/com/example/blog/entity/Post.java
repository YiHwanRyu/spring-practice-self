package com.example.blog.entity;

import com.example.blog.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Table(name = "post")
@NoArgsConstructor
public class Post extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "content", nullable = false, length = 500)
    private String content;
    @Column(nullable = false)
    private String titleImgUrl;
    @Column(nullable = false)
    private String subImgUrl1;
    @Column(nullable = false)
    private String subImgUrl2;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @OrderBy("createdAt DESC")
    private List<Comment> commentList = new ArrayList<>();

    public Post(PostRequestDto requestDto, String username, String titleImgUrl, String subImgUrl1, String subImgUrl2) {
        this.title = requestDto.getTitle();
        this.username = username;
        this.content = requestDto.getContent();
        this.titleImgUrl = titleImgUrl;
        this.subImgUrl1 = subImgUrl1;
        this.subImgUrl2 = subImgUrl2;
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }
}
