package com.example.blog.service;

import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import com.example.blog.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogService {
    private BlogRepository blogRepository;

    //@Autowired // 생성자 1개일 때는 생략가능
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public PostResponseDto createPost(PostRequestDto requestDto) {
        // RequsetDto -> Entity(데이터베이스 교환 객체)
        Post post = new Post(requestDto);
        // DB 저장
        Post savePost = blogRepository.save(post);
        // Entity -> ResponseDto
        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }

    public List<PostResponseDto> getPosts() {
        return blogRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    public PostResponseDto getPost(Long id) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);
        // responseDto 로 반환
        return new PostResponseDto(post);
    }

    @Transactional // 변경 적용하기 위해
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);
        // post 수정(영속성 컨텍스트의 변경감지를 통해, 즉, requestDto에 들어온 객체로 post 객체(entity)를 업데이트 시킴)
        if(requestDto.getPassword().equals(post.getPassword())) {
            post.update(requestDto);
        } else {
            return new PostResponseDto(post);
        }
        // responseDto 로 반환
        return new PostResponseDto(post);
    }

    public Boolean deletePost(Long id, String password) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);
        // post 삭제
        if(post.getPassword().equals(password)) {
            blogRepository.delete(post);
        } else {
            return false;
        }
        // boolean으로 반환
        return true;
    }

    private Post findPost(Long id) {
        return blogRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 포스트는 존재하지 않습니다.")
        );
    }

}
