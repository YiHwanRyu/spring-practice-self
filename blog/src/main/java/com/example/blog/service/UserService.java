package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.User;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public MessageResponseDto createUser(SignUpRequestDto signUpRequestDto) {
        String username = signUpRequestDto.getUsername();
        String password = signUpRequestDto.getPassword();

        // username 중복 확인
        if(userRepository.findById(username).isPresent()) { // Optional 객체로 반환되어 isPresent 사용
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        // 중복 없으면 저장
        User user = new User(username, password);
        userRepository.save(user);

        // 저장 성공하면 메세지와 상태코드 반환(HttpStatus로 이후 Refactor 가능할 듯)
        return new MessageResponseDto("회원가입 성공", "200");
    }


    public MessageResponseDto loginUser(LoginRequestDto loginRequestDto, HttpServletResponse res) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        //username 확인(저장된 회원의 유무 확인)
        User user = userRepository.findById(username).orElseThrow(() ->
                        new NullPointerException("없는 사용자입니다.")
                    );
        //password 일치 확인(저장된 회원이 있을 때 비밀번호 비교)
        if(!password.equals(user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성 및 쿠키에 저장 후 Response객체에 추가 -> 자동으로 쿠키도 전달된다.
        String token = jwtUtil.createToken(user.getUsername());
        jwtUtil.addJwtToCookie(token, res);

        return new MessageResponseDto("로그인 성공", "200");
    }
}
