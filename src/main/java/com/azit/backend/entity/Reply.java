package com.azit.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // 대댓글 로직 편의를 위해 Setter 사용
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reply")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // 어느 글의 댓글인지

    // ★ 대댓글의 핵심: 내 부모 댓글이 누구냐?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Reply parent;

    // 내 자식 댓글들
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Builder.Default
    private List<Reply> children = new ArrayList<>();

    // 삭제된 댓글인지 여부 ("삭제된 댓글입니다" 표시용)
    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    @CreationTimestamp
    private LocalDateTime createdAt;
}