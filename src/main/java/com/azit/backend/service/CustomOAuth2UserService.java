package com.azit.backend.service;

import com.azit.backend.entity.Member;
import com.azit.backend.entity.Role; // ★ 우리가 만든 Role 임포트
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
        System.out.println("구글 정보 도착: " + oauth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member member;
        if (optionalMember.isEmpty()) {
            System.out.println("신규 회원입니다. 회원가입 진행...");
            member = Member.builder()
                    .email(email)
                    .nickname(name)
                    .profileImg(picture)
                    .role(Role.USER) // ★ Member.java를 고쳤기 때문에 이제 에러 안 남!
                    .provider(provider)
                    .providerId(providerId)
                    .password("")
                    .build();
            memberRepository.save(member);
        } else {
            System.out.println("기존 회원입니다. 로그인 진행...");
            member = optionalMember.get();
        }

        return oauth2User;
    }
}