package com.azit.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누구한테 보내는 알림인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String message; // 알림 내용 (예: '비트코인' 관련 새 뉴스가 떴습니다!)

    @Column(columnDefinition = "TEXT")
    private String link;    // 클릭하면 이동할 뉴스 주소

    private boolean isRead; // 읽었는지 안 읽었는지 (false: 안 읽음/빨간점)

    @CreationTimestamp
    private LocalDateTime createdAt;
}