package com.azit.backend.controller;

import com.azit.backend.dto.SignUpRequest;
import com.azit.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager; // ★ 로그인 관리자 주입

    // 1. 홈 화면 (확인용)
    @GetMapping("/")
    public String home() {
        return "🎉 AZ News Azit 백엔드 서버 정상 작동 중!";
    }

    // 2. 내 정보 확인
    @GetMapping("/my-info")
    public Object myInfo(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();

        // 구글 로그인 유저는 OAuth2User 반환
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttributes();
        }

        // 일반 로그인 유저는 Member Entity 정보를 DTO로 변환해서 주는 게 좋지만,
        // 일단 간단하게 Principal(UserDetails) 정보 반환 (비번 제외됨)
        return principal;
    }

    // 3. 회원가입
    @PostMapping("/api/auth/signup")
    public String signup(@RequestBody SignUpRequest request) {
        try {
            authService.signUp(request);
            return "회원가입 성공!";
        } catch (Exception e) {
            throw new RuntimeException("가입 실패: " + e.getMessage());
        }
    }

    // ★ 4. 일반 로그인 (여기가 새로 추가된 핵심!)
    @PostMapping("/api/auth/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        // 1. 아이디/비번으로 인증 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        // 2. 관리자에게 검사 맡기기 (틀리면 여기서 에러 남)
        Authentication authentication = authenticationManager.authenticate(token);

        // 3. 인증 성공 시, 시큐리티 컨텍스트에 저장 (세션 생성)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 세션에 명시적으로 저장 (이게 있어야 계속 로그인 상태 유지됨)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return "로그인 성공";
    }

    // 로그인 요청 받을 DTO
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}