package com.azit.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feed")
public class Feed {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_public", columnDefinition = "boolean default true")
    @Builder.Default
    private boolean isPublic = true;

    // ★ [여기 추가!] 원본 피드 (리트윗인 경우)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_feed_id")
    private Feed originalFeed;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedHeart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedComment> feedComments = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void update(String content) {
        this.content = content;
    }
}