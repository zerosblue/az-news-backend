package com.azit.backend.controller;

import com.azit.backend.entity.Member;
import com.azit.backend.entity.Notification;
import com.azit.backend.repository.MemberRepository;
import com.azit.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    // 1. 안 읽은 알림 개수 확인 (빨간 점용)
    @GetMapping("/api/notifications/unread-count")
    public long getUnreadCount(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return 0;
        Member member = getMember(principal);
        return notificationRepository.countByMemberIdAndIsReadFalse(member.getId());
    }

    // 2. 내 알림 목록 보기
    @GetMapping("/api/notifications")
    public List<Notification> getNotifications(@AuthenticationPrincipal OAuth2User principal) {
        Member member = getMember(principal);
        return notificationRepository.findByMemberIdOrderByIdDesc(member.getId());
    }

    // 3. 알림 읽음 처리 (클릭하면 빨간 점 사라지게)
    @PostMapping("/api/notifications/{id}/read")
    public void readNotification(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setRead(true);
        notificationRepository.save(n);
    }

    private Member getMember(OAuth2User principal) {
        String email = principal.getAttribute("email");
        return memberRepository.findByEmail(email).orElseThrow();
    }
}