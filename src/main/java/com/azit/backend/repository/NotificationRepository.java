package com.azit.backend.repository;

import com.azit.backend.entity.Member; // Member 임포트 추가
import com.azit.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 안 읽은 알림 개수
    long countByMemberIdAndIsReadFalse(Long memberId);

    // 내 알림 목록 (최신순)
    List<Notification> findByMemberIdOrderByIdDesc(Long memberId);

    // ★ [추가된 핵심 기능] "이 멤버한테 이 링크로 보낸 적 있니?" (중복 검사용)
    boolean existsByMemberAndLink(Member member, String link);
}