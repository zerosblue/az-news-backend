package com.azit.backend.repository;

import com.azit.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원이 있는지 찾는 기능
    Optional<Member> findByEmail(String email);
}