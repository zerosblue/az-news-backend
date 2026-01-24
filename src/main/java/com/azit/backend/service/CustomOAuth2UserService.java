package com.azit.backend.service;

import com.azit.backend.entity.Member;
import com.azit.backend.entity.Role;
import com.azit.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member member;
        if (optionalMember.isEmpty()) {
            // 1. 신규 가입이면 -> 구글 정보로 저장
            System.out.println("신규 회원입니다. 회원가입 진행...");
            member = Member.builder()
                    .email(email)
                    .nickname(name) // 처음엔 구글 이름 사용
                    .profileImg(picture) // 처음엔 구글 사진 사용
                    .role(Role.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .password("")
                    .build();
            memberRepository.save(member);
        } else {
            // ★ 2. 기존 회원이면 -> 로그인만 시킴 (절대 정보 덮어쓰기 금지!)
            System.out.println("기존 회원입니다. 로그인 진행...");
            member = optionalMember.get();

            // 여기서 setNickname, setProfileImg를 하면 안 됨!
            // 그냥 아무것도 안 하고 넘어가야 네가 수정한 정보가 유지됨.
        }

        return oauth2User;
    }
}