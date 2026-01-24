package com.azit.backend.service;

import com.azit.backend.dto.MemberDto;
import com.azit.backend.entity.Member;
import com.azit.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 프로젝트 경로 + /uploads/profile/ (프로필 사진은 따로 관리하면 좋음)
    // 귀찮으면 그냥 uploads/ 에 넣어도 됨. 여기선 uploads/ 로 통일할게.
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    // 1. 내 정보 가져오기
    @Transactional(readOnly = true)
    public MemberDto.Response getMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));
        return new MemberDto.Response(member);
    }

    // 2. 프로필 수정 (닉네임 + 사진)
    @Transactional
    public MemberDto.Response updateProfile(String email, String nickname, MultipartFile file) throws IOException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 닉네임 변경
        if (nickname != null && !nickname.isEmpty()) {
            member.setNickname(nickname);
        }

        // 사진 변경
        if (file != null && !file.isEmpty()) {
            // 폴더 확인
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            // 파일 저장
            String originalName = file.getOriginalFilename();
            String saveName = UUID.randomUUID() + "_" + originalName;
            file.transferTo(new File(uploadDir + saveName));

            // DB에 경로 저장 (기존 파일 삭제 로직은 복잡하니 생략, 덮어쓰기 느낌으로)
            member.setProfileImg("/images/" + saveName);
        }

        memberRepository.save(member);
        return new MemberDto.Response(member);
    }
}