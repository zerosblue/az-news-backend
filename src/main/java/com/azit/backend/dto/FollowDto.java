package com.azit.backend.dto;

import com.azit.backend.entity.Member;
import lombok.Data;

@Data
public class FollowDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImg;
    private boolean isFollowedByMe; // 내가 이 사람을 구독 중인가?

    public FollowDto(Member member, boolean isFollowedByMe) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImg = member.getProfileImg();
        this.isFollowedByMe = isFollowedByMe;
    }
    // 팔로우 목록용 DTO
    @Data
    public static class FollowResponse {
        private String email;
        private String nickname;
        private String profileImg;
        private boolean isFollowedByMe;

        public FollowResponse(com.azit.backend.entity.Member m, boolean isFollowed) {
            this.email = m.getEmail();
            this.nickname = m.getNickname();
            this.profileImg = m.getProfileImg();
            this.isFollowedByMe = isFollowed;
        }
    }
}
