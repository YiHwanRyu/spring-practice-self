package com.example.blog.service;

import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.UserRoleEnum;
import com.example.blog.repository.PostRepository;
import com.example.blog.utils.AwsS3Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AwsS3Util awsS3Util;

    //@Autowired // 생성자 1개일 때는 생략가능
    public PostService(PostRepository postRepository, AwsS3Util awsS3Util) {
        this.postRepository = postRepository;
        this.awsS3Util = awsS3Util;
    }

    public PostResponseDto createPost(String username, PostRequestDto requestDto, MultipartFile titleImgMultiPartFile, MultipartFile subImg1MultiPartFile, MultipartFile subImg2MultiPartFile) {
        //urls
        String titleImgUrl;
        String subImgurl1 = "";
        String subImgurl2 = "";

        // s3에 이미지 파일 저장 및 url
        titleImgUrl = awsS3Util.uploadImgFile(titleImgMultiPartFile, "postImg");
        if(!subImg1MultiPartFile.isEmpty()) {
            subImgurl1 = awsS3Util.uploadImgFile(subImg1MultiPartFile, "postImg");
        }
        if(!subImg2MultiPartFile.isEmpty()) {
            subImgurl2 = awsS3Util.uploadImgFile(subImg2MultiPartFile, "postImg");
        }

        // RequsetDto -> Entity(데이터베이스 교환 객체)
        Post post = new Post(requestDto, username, titleImgUrl, subImgurl1, subImgurl2);
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
    public PostResponseDto updatePost(String role, String username, Long id, PostRequestDto requestDto) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);

        // 사용자가 ADMIN 권한이거나 작성자일 때만 수정이 가능
        if(!(role.equals(UserRoleEnum.ADMIN.toString()) || username.equals(post.getUsername()))) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        // post 수정(영속성 컨텍스트의 변경감지를 통해, 즉, requestDto에 들어온 객체로 post 객체(entity)를 업데이트 시킴)
        post.update(requestDto);

        // responseDto 로 반환
        return new PostResponseDto(post);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> deletePost(String role, String username, Long id) {
        // 해당 post가 DB에 존재하는지 확인
        Post post = findPost(id);

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
