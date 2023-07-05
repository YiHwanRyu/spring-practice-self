package com.example.blogram.domain.posts.repository;

import com.example.blogram.domain.posts.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
}
