package com.azit.backend.repository;

import com.azit.backend.entity.Follow;
import com.azit.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 1. 팔로우 여부 확인 (follower: 나, following: 상대방)
    boolean existsByFollowerAndFollowing(Member follower, Member following);

    // 2. 팔로우 취소할 때 데이터 찾기
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    // 3. 내가 팔로우한 목록 (내 아이디가 follower 컬럼에 있는 것들)
    List<Follow> findByFollower(Member follower);

    // 4. 나를 팔로우한 목록 (내 아이디가 following 컬럼에 있는 것들)
    List<Follow> findByFollowing(Member following);
}