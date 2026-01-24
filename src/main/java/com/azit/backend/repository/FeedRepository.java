package com.azit.backend.repository;

import com.azit.backend.entity.Feed;
import com.azit.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // 1. 전체 피드 최신순
    List<Feed> findAllByOrderByCreatedAtDesc();

    // 2. 특정 유저들(팔로잉)의 피드만 최신순
    List<Feed> findByMemberInOrderByCreatedAtDesc(List<Member> members);

    // 3. 내 피드만 보기 (마이페이지용)
    List<Feed> findByMemberOrderByCreatedAtDesc(Member member);
}