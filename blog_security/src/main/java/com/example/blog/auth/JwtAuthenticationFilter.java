package com.example.blog.auth;

import com.example.blog.dto.LoginRequestDto;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.entity.UserRoleEnum;
import com.example.blog.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
    }
    // 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    // 로그인 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(username, role); // 토큰 생성
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token); // 토큰을 헤더에 담음
        response.setStatus(HttpServletResponse.SC_OK); // 상태코드
        response.setContentType("application/json"); // json 형태로 body반환하기 위한 헤더
        response.setCharacterEncoding("UTF-8"); // utf-8 지정

        ObjectMapper objectMapper = new ObjectMapper(); // 객체를 json형태로 변환하기 위해.
        MessageResponseDto messageResponseDto = new MessageResponseDto("로그인 성공", HttpStatus.OK.toString()); // body에 넣을 데이터
        response.getWriter().write(objectMapper.writeValueAsString(messageResponseDto)); // json형태로 body에 반환
    }
    // 로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 상태코드
        response.setContentType("application/json"); // json 형태로 body반환하기 위한 헤더
        response.setCharacterEncoding("UTF-8"); // utf-8 지정

        ObjectMapper objectMapper = new ObjectMapper(); // 객체를 json형태로 변환하기 위해.
        MessageResponseDto messageResponseDto = new MessageResponseDto("로그인 실패", HttpStatus.BAD_REQUEST.toString()); // body에 넣을 데이터
        response.getWriter().write(objectMapper.writeValueAsString(messageResponseDto)); // json형태로 body에 반환
    }
}
