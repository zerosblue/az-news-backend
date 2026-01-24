package com.azit.backend.dto;

import com.azit.backend.entity.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    // 1. 프로필 수정 요청할 때 쓸 그릇
    @Data
    public static class UpdateRequest {
        private String nickname;
    }

    // 2. 내 정보 보여줄 때 쓸 그릇
    @Data
    public static class Response {
        private Long id;
        private String email;
        private String nickname;
        private String profileImg;
        private String interests; // "주식,골프"

        public Response(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.nickname = member.getNickname();
            this.profileImg = member.getProfileImg();
            this.interests = member.getInterests();
        }
    }
}