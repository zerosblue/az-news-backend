package com.azit.backend.controller;

import com.azit.backend.dto.FeedDto;
import com.azit.backend.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // 1. 피드 작성
    @PostMapping
    public ResponseEntity<Map<String, String>> create(
            Authentication authentication,
            @RequestPart("data") FeedDto.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        feedService.createFeed(getEmailFromAuth(authentication), request, files);
        return ResponseEntity.ok(Map.of("message", "작성 완료"));
    }

    // 2. 피드 목록 조회
    @GetMapping
    public List<FeedDto.Response> list(
            @RequestParam(defaultValue = "global") String type,
            Authentication authentication
    ) {
        String email = (authentication != null) ? getEmailFromAuth(authentication) : null;
        return feedService.getFeeds(email, type);
    }

    // 3. 좋아요 토글
    @PostMapping("/{id}/heart")
    public boolean heart(@PathVariable Long id, Authentication authentication) {
        return feedService.toggleHeart(id, getEmailFromAuth(authentication));
    }

    // 4. 피드 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        feedService.deleteFeed(id, getEmailFromAuth(authentication));
        return "삭제 완료";
    }

    // 5. 피드 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            Authentication authentication,
            @RequestPart("data") FeedDto.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        feedService.updateFeed(id, getEmailFromAuth(authentication), request, files);
        return ResponseEntity.ok(Map.of("message", "수정 완료"));
    }

    // 6. 이미지 개별 삭제
    @DeleteMapping("/image/{imageId}")
    public String deleteImage(@PathVariable Long imageId, Authentication authentication) {
        feedService.deleteFeedImage(imageId, getEmailFromAuth(authentication));
        return "이미지 삭제 완료";
    }

    // 7. 팔로우 토글
    @PostMapping("/follow/{targetEmail}")
    public boolean follow(@PathVariable String targetEmail, Authentication authentication) {
        return feedService.toggleFollow(getEmailFromAuth(authentication), targetEmail);
    }

    // 8. 댓글 작성
    @PostMapping("/{id}/comment")
    public ResponseEntity<Map<String, String>> writeComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication authentication
    ) {
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;

        feedService.writeComment(id, getEmailFromAuth(authentication), content, parentId);
        return ResponseEntity.ok(Map.of("message", "댓글 작성 완료"));
    }

    // ★★★ [여기가 문제였어! 이 두 개가 꼭 있어야 해!] ★★★

    // 9. 팔로워 목록 (나를 구독한 사람)
    @GetMapping("/followers")
    public List<FeedDto.FollowResponse> getFollowers(Authentication authentication) {
        return feedService.getFollowers(getEmailFromAuth(authentication));
    }

    // 10. 팔로잉 목록 (내가 구독한 사람)
    @GetMapping("/followings")
    public List<FeedDto.FollowResponse> getFollowings(Authentication authentication) {
        return feedService.getFollowings(getEmailFromAuth(authentication));
    }

    // ★★★ --------------------------------------- ★★★

    // 11. 댓글 수정
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, String>> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        feedService.updateComment(commentId, getEmailFromAuth(authentication), body.get("content"));
        return ResponseEntity.ok(Map.of("message", "댓글 수정 완료"));
    }

    // 12. 댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        feedService.deleteComment(commentId, getEmailFromAuth(authentication));
        return ResponseEntity.ok(Map.of("message", "댓글 삭제 완료"));
    }

    // [도우미] 이메일 추출
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        if (principal instanceof Map) {
            return (String) ((Map<?, ?>) principal).get("email");
        }
        return authentication.getName();
    }
    // 13. 리트윗
    @PostMapping("/{id}/retweet")
    public ResponseEntity<Map<String, String>> retweet(
            @PathVariable Long id,
            Authentication authentication
    ) {
        feedService.retweet(id, getEmailFromAuth(authentication));
        return ResponseEntity.ok(Map.of("message", "리트윗 완료"));
    }
}