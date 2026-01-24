package com.azit.backend.controller;

import com.azit.backend.entity.Member;
import com.azit.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final MemberRepository memberRepository;

    // 1. 내 키워드 목록 가져오기
    @GetMapping("/api/keywords")
    public List<String> getKeywords(Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));
        return parseKeywords(member.getInterests());
    }

    // 2. 키워드 추가하기
    @PostMapping("/api/keywords")
    public List<String> addKeyword(Authentication authentication, @RequestBody Map<String, String> body) {
        String email = getEmailFromAuth(authentication);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));

        String newKeyword = body.get("keyword");

        // 빈 값 체크
        if (newKeyword == null || newKeyword.trim().isEmpty()) {
            return parseKeywords(member.getInterests());
        }

        List<String> keywords = parseKeywords(member.getInterests());

        // 중복 체크
        if (!keywords.contains(newKeyword.trim())) {
            keywords.add(newKeyword.trim());
            updateMemberInterests(member, keywords);
        }
        return keywords;
    }

    // 3. 키워드 삭제하기
    @DeleteMapping("/api/keywords")
    public List<String> deleteKeyword(Authentication authentication, @RequestParam String keyword) {
        String email = getEmailFromAuth(authentication);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));

        List<String> keywords = parseKeywords(member.getInterests());
        keywords.remove(keyword);
        updateMemberInterests(member, keywords);

        return keywords;
    }

    // --- 도우미 함수들 ---

    // ★ [핵심] 로그인 유형에 상관없이 이메일 꺼내기
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        Object principal = authentication.getPrincipal();

        // 구글 로그인
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        // 일반 로그인 (UserDetails 또는 Map)
        if (principal instanceof Map) {
            return (String) ((Map<?, ?>) principal).get("email");
        }

        // 그 외 (기본)
        return authentication.getName();
    }

    private List<String> parseKeywords(String interests) {
        if (interests == null || interests.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.stream(interests.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));
    }

    private void updateMemberInterests(Member member, List<String> keywords) {
        String newInterests = String.join(",", keywords);
        member.setInterests(newInterests);
        memberRepository.save(member);
    }
}