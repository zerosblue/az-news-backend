package com.azit.backend.controller;

import com.azit.backend.dto.NewsDto;
import com.azit.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    // 1. 목록 조회
    @GetMapping
    public List<NewsDto.Response> list(@RequestParam(required = false) String category) {
        return newsService.getNewsList(category);
    }

    // 2. 상세 조회 (댓글 보기용)
    @GetMapping("/{id}")
    public NewsDto.Response detail(@PathVariable Long id) {
        return newsService.getNewsDetail(id);
    }

    // 3. 댓글 작성
    @PostMapping("/{id}/comment")
    public ResponseEntity<Map<String, String>> writeComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        String email = getEmailFromAuth(authentication);
        newsService.writeComment(id, email, body.get("content"));
        return ResponseEntity.ok(Map.of("message", "댓글 작성 완료"));
    }

    // 도우미 함수
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) throw new RuntimeException("로그인 필요");
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        if (principal instanceof Map) return (String) ((Map<?, ?>) principal).get("email");
        return authentication.getName();
    }
}