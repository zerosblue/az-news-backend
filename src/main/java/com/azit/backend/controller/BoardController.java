package com.azit.backend.controller;

import com.azit.backend.dto.BoardDto;
import com.azit.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 1. 글 쓰기
    @PostMapping
    public String write(
            Authentication authentication,
            @RequestPart("data") BoardDto.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        String email = getEmailFromAuth(authentication);
        boardService.writeBoard(email, request, files);
        return "작성 완료";
    }

    // 2. 목록 조회
    @GetMapping
    public List<BoardDto.Response> getList(@RequestParam(required = false) String category) {
        return boardService.getBoardList(category);
    }

    // 3. 상세 조회
    @GetMapping("/{id}")
    public BoardDto.Response getDetail(@PathVariable Long id) {
        return boardService.getBoardDetail(id);
    }

    // 4. 글 수정 (파일 업로드 때문에 @PostMapping으로 변경하는 게 정신건강에 좋음, 하지만 PUT 유지하며 구조 변경)
    // 주의: 프론트에서 FormData로 보낼 것임
    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            Authentication authentication,
            @RequestPart("data") BoardDto.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        String email = getEmailFromAuth(authentication);
        boardService.updateBoard(id, email, request, files);
        return "수정 완료";
    }

    // 5. 글 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        boardService.deleteBoard(id, email);
        return "삭제 완료";
    }

    // 6. 댓글 작성
    @PostMapping("/{id}/reply")
    public String writeReply(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody Map<String, Object> body
    ) {
        String email = getEmailFromAuth(authentication);
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        boardService.writeReply(id, email, content, parentId);
        return "댓글 작성 완료";
    }

    // ★ 7. 이미지 개별 삭제
    @DeleteMapping("/image/{imageId}")
    public String deleteImage(@PathVariable Long imageId, Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        boardService.deleteImage(imageId, email);
        return "이미지 삭제 완료";
    }

    // --- 도우미 함수 ---
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null) throw new RuntimeException("로그인이 필요합니다.");
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