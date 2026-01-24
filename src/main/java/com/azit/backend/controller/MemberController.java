package com.azit.backend.controller;

import com.azit.backend.dto.MemberDto;
import com.azit.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    // 1. 내 정보 조회
    @GetMapping("/me")
    public MemberDto.Response getMyInfo(Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        return memberService.getMyInfo(email);
    }

    // 2. 프로필 수정 (PUT)
    @PutMapping("/me")
    public MemberDto.Response updateProfile(
            Authentication authentication,
            @RequestPart(value = "data", required = false) MemberDto.UpdateRequest request, // 닉네임
            @RequestPart(value = "file", required = false) MultipartFile file // 프로필 사진
    ) throws IOException {
        String email = getEmailFromAuth(authentication);
        String nickname = request != null ? request.getNickname() : null;
        return memberService.updateProfile(email, nickname, file);
    }

    // 이메일 추출 도우미 함수 (복붙)
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) throw new RuntimeException("로그인 필요");
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        if (principal instanceof Map) {
            return (String) ((Map<?, ?>) principal).get("email");
        }
        return authentication.getName();
    }
}