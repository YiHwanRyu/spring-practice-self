package com.example.blog.service;

import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.UserRoleEnum;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private JwtUtil jwtUtil;

    //@Autowired // 생성자 1개일 때는 생략가능
    public PostService(PostRepository postRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.jwtUtil = jwtUtil;
    }

    public PostResponseDto createPost(String tokenValue, PostRequestDto requestDto) {
        // JWT 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);

        // 토큰 검증
        if(!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // username
        String username = info.getSubject();

        // RequsetDto -> Entity(데이터베이스 교환 객체)
        Post post = new Post(requestDto, username);
        // DB 저장
        Post savePost = postRepository.save(post);
        // Entity -> ResponseDto
        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }

    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    public PostResponseDto getPost(Long id) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);
        // responseDto 로 반환
        return new PostResponseDto(post);
    }

    @Transactional // 변경 적용하기 위해
    public PostResponseDto updatePost(String tokenValue, Long id, PostRequestDto requestDto) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);

        // JWT 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);

        // 토큰 검증
        if(!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // username
        String username = info.getSubject();

        // role
        String role = info.get(JwtUtil.AUTHORIZATION_KEY, String.class);

        // 사용자가 ADMIN 권한이거나 작성자일 때만 수정이 가능
        if(!(role.equals(UserRoleEnum.ADMIN.toString()) || username.equals(post.getUsername()))) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        // post 수정(영속성 컨텍스트의 변경감지를 통해, 즉, requestDto에 들어온 객체로 post 객체(entity)를 업데이트 시킴)
        post.update(requestDto);

        // responseDto 로 반환
        return new PostResponseDto(post);
    }


    public ResponseEntity<MessageResponseDto> deletePost(String tokenValue, Long id) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);

        // JWT 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);

        // 토큰 검증
        if(!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // username
        String username = info.getSubject();

        // role
        String role = info.get(JwtUtil.AUTHORIZATION_KEY, String.class);

        // 사용자가 ADMIN 권한이거나 작성자일 때만 수정이 가능
        if(!(role.equals(UserRoleEnum.ADMIN.toString()) || username.equals(post.getUsername()))) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);

        return new ResponseEntity<>(new MessageResponseDto("게시글 삭제 성공", "200"), HttpStatus.OK);
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 포스트는 존재하지 않습니다.")
        );
    }

}
