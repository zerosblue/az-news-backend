package com.azit.backend.controller;

import com.azit.backend.dto.MemberDto;
import com.azit.backend.dto.SignUpRequest;
import com.azit.backend.entity.Member;
import com.azit.backend.repository.MemberRepository;
import com.azit.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository; // ★ DB 조회를 위해 추가

    // 1. 홈 화면
    @GetMapping("/")
    public String home() {
        return "🎉 AZ News Azit 백엔드 서버 정상 작동 중!";
    }

    // ★ 2. 내 정보 확인 (핵심 수정!)
    // 세션 정보 대신, DB에 있는 최신 정보를 줘야 함
    @GetMapping("/my-info")
    public MemberDto.Response myInfo(Authentication authentication) {
        if (authentication == null) return null;

        String email = getEmailFromAuth(authentication);

        // DB에서 최신 정보 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));

        // DTO로 예쁘게 포장해서 리턴 (여기에 수정된 닉네임/사진이 들어있음)
        return new MemberDto.Response(member);
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

    // 4. 일반 로그인
    @PostMapping("/api/auth/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return "로그인 성공";
    }

    // 도우미 함수 (이메일 추출)
    private String getEmailFromAuth(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        if (principal instanceof Map) {
            return (String) ((Map<?, ?>) principal).get("email");
        }
        return authentication.getName();
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}