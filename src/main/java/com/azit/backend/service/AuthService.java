package com.azit.backend.service;

import com.azit.backend.dto.SignUpRequest;
import com.azit.backend.entity.Member;
import com.azit.backend.entity.Role;
import com.azit.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService { // ★ UserDetailsService 구현

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 회원가입
    public void signUp(SignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setNickname(request.getNickname());
        member.setInterests(request.getInterests());
        member.setRole(Role.USER);
        member.setProvider("local");

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        member.setPassword(encodedPassword);

        memberRepository.save(member);
    }

    // ★ 2. 로그인 시 비밀번호 검사하는 로직 (스프링 시큐리티가 자동 호출함)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // Member 정보를 시큐리티가 이해하는 User 객체로 변환해서 리턴
        return new User(
                member.getEmail(),
                member.getPassword(),
                Collections.emptyList() // 권한 리스트 (일단 비워둠)
        );
    }
}