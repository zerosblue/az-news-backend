package com.azit.backend.dto;

import com.azit.backend.entity.Feed;
import com.azit.backend.entity.FeedComment;
import com.azit.backend.entity.Member;
import lombok.Data;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FeedDto {

    @Data
    public static class Request { private String content; }

    @Data
    public static class Response {
        private Long id;
        private String content;
        private String writerName;
        private String writerEmail;
        private String writerProfile;
        private String createdAt;
        private List<ImageInfo> images;
        private int heartCount;
        private boolean isHearted;
        private boolean isFollowed;
        private List<CommentResponse> comments;

        // ★★★ [이게 빠져서 에러 났던 거야!] ★★★
        private Response originalFeed;

        public Response(Feed feed, boolean isHearted, boolean isFollowed) {
            this.id = feed.getId();
            this.content = feed.getContent();

            if (feed.getMember() != null) {
                this.writerName = feed.getMember().getNickname();
                this.writerEmail = feed.getMember().getEmail();
                this.writerProfile = feed.getMember().getProfileImg();
            } else {
                this.writerName = "알 수 없음";
                this.writerEmail = "";
                this.writerProfile = null;
            }

            this.createdAt = feed.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));

            this.images = feed.getImages().stream()
                    .map(img -> new ImageInfo(img.getId(), img.getImgUrl()))
                    .collect(Collectors.toList());

            this.heartCount = feed.getHearts().size();
            this.isHearted = isHearted;
            this.isFollowed = isFollowed;

            // 댓글 변환 (부모 없는 것만)
            this.comments = feed.getFeedComments().stream()
                    .filter(c -> c.getParent() == null)
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());

            // ★ 리트윗 정보 담기 (이제 변수가 있으니 에러 안 남!)
            if (feed.getOriginalFeed() != null) {
                // 원본 글의 좋아요/팔로우 여부는 false로 (단순 표시용)
                this.originalFeed = new Response(feed.getOriginalFeed(), false, false);
            }
        }
    }

    @Data
    public static class ImageInfo {
        private Long id;
        private String url;
        public ImageInfo(Long id, String url) { this.id = id; this.url = url; }
    }

    @Data
    public static class CommentResponse {
        private Long id;
        private String content;
        private String writerName;
        private String writerEmail; // 작성자 이메일 추가됨 (본인 확인용)
        private String createdAt;
        private List<CommentResponse> children;

        public CommentResponse(FeedComment c) {
            this.id = c.getId();
            this.content = c.getContent();
            this.writerName = c.getMember().getNickname();
            this.writerEmail = c.getMember().getEmail();
            this.createdAt = c.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            this.children = c.getChildren().stream()
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class FollowResponse {
        private String email;
        private String nickname;
        private String profileImg;
        private boolean isFollowedByMe;

        public FollowResponse(Member m, boolean isFollowed) {
            this.email = m.getEmail();
            this.nickname = m.getNickname();
            this.profileImg = m.getProfileImg();
            this.isFollowedByMe = isFollowed;
        }
    }
}