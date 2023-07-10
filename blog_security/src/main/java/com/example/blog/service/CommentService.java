package com.example.blog.service;

import com.example.blog.dto.CommentRequestDto;
import com.example.blog.dto.CommentResponseDto;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.UserRoleEnum;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private JwtUtil jwtUtil;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
    }

    // 댓글 생성
    public CommentResponseDto createComment(String tokenValue, CommentRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        // 선택한 post 찾아옴
        Post post = findPost(requestDto.getPostId());

        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // username
        String username = info.getSubject();

        // 댓글 객체
        Comment comment = new Comment(requestDto, username);

        // 게시글과 연관관계 설정
        comment.connectPost(post);

        // 저장
        Comment saveComment = commentRepository.save(comment);
        return new CommentResponseDto(saveComment);
    }

    @Transactional
    public CommentResponseDto updateComment(String tokenValue, Long id, CommentRequestDto requestDto) {
        Comment comment = findComment(id);

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

        if(!(role.equals(UserRoleEnum.ADMIN.toString()) || username.equals(comment.getUsername()))){
            throw new IllegalArgumentException("해당 댓글을 작성한 사용자나 관리자가 아닙니다.");
        }

        comment.update(requestDto);

        return new CommentResponseDto(comment);
    }


    public ResponseEntity<MessageResponseDto> deleteComment(String tokenValue, Long id) {
        Comment comment = findComment(id);

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

        if(!(role.equals(UserRoleEnum.ADMIN.toString()) || username.equals(comment.getUsername()))){
            throw new IllegalArgumentException("해당 댓글을 작성한 사용자나 관리자가 아닙니다.");
        }

        commentRepository.delete(comment);

        return new ResponseEntity<>(new MessageResponseDto("댓글 삭제 성공", "200"), HttpStatus.OK);
    }

    private Comment findComment(Long id){
        return commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택하신 댓글은 존재하지 않습니다.")
        );
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 포스트는 존재하지 않습니다.")
        );
    }

}
