package com.azit.backend.dto;

import com.azit.backend.entity.Board;
import com.azit.backend.entity.Reply;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BoardDto {

    @Data
    public static class Request {
        private String title;
        private String content;
        private String category;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String category;
        private String writerName;
        private String writerEmail; // 본인 확인용
        private int viewCount;
        private String createdAt;

        // ★ 여기가 변경됨! (String 리스트 -> ImageInfo 리스트)
        private List<ImageInfo> images;

        private List<ReplyDto> replies;

        public Response(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.category = board.getCategory();
            this.writerName = board.getMember().getNickname();
            this.writerEmail = board.getMember().getEmail();
            this.viewCount = board.getViewCount();
            this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            // ★ 이미지 정보 매핑
            this.images = board.getImages().stream()
                    .map(img -> new ImageInfo(img.getId(), img.getImgUrl()))
                    .collect(Collectors.toList());

            if (board.getReplies() != null) {
                this.replies = board.getReplies().stream()
                        .filter(reply -> reply.getParent() == null)
                        .map(ReplyDto::new)
                        .collect(Collectors.toList());
            } else {
                this.replies = List.of();
            }
        }
    }

    // ★ 이미지 정보 담을 작은 클래스
    @Data
    public static class ImageInfo {
        private Long id;
        private String url;

        public ImageInfo(Long id, String url) {
            this.id = id;
            this.url = url;
        }
    }
}