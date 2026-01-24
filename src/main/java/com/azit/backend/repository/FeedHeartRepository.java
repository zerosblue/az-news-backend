package com.azit.backend.repository;

import com.azit.backend.entity.Feed;
import com.azit.backend.entity.FeedHeart;
import com.azit.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FeedHeartRepository extends JpaRepository<FeedHeart, Long> {
    // 내가 이 글에 좋아요 눌렀나?
    boolean existsByMemberAndFeed(Member member, Feed feed);

    // 좋아요 취소할 때 찾기
    Optional<FeedHeart> findByMemberAndFeed(Member member, Feed feed);
}