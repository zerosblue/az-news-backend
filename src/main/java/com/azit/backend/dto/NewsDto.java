package com.azit.backend.dto;

import com.azit.backend.entity.News;
import com.azit.backend.entity.NewsComment;
import lombok.Data;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class NewsDto {

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String link;
        private String category;
        private String provider;
        private String pubDate;

        // ★★★ [이 줄이 빠져서 에러가 났던 거야!] ★★★
        private String description;

        private List<CommentResponse> comments;

        public Response(News news) {
            this.id = news.getId();
            this.title = news.getTitle();
            this.link = news.getLink();
            this.category = news.getCategory();
            this.provider = news.getProvider();
            this.pubDate = news.getPubDate() != null ? news.getPubDate().toString() : "";

            // 이제 변수를 선언했으니 에러가 안 날 거야!
            this.description = news.getDescription();

            // 댓글 변환
            this.comments = news.getComments().stream()
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class CommentResponse {
        private Long id;
        private String content;
        private String writerName;
        private String createdAt;

        public CommentResponse(NewsComment c) {
            this.id = c.getId();
            this.content = c.getContent();
            this.writerName = c.getMember().getNickname();
            this.createdAt = c.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }
}