package com.azit.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "news") // DB 테이블 이름이랑 똑같아야 해
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String link;

    private String category;
    private String provider;

    @Column(columnDefinition = "TEXT") // ★ 추가
    private String description;

    @Column(name = "pub_date") // DB 컬럼명은 pub_date (스네이크 케이스)
    private Date pubDate;     // 자바에서는 pubDate (카멜 케이스)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ★ [추가] 뉴스에 달린 댓글들
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<NewsComment> comments = new java.util.ArrayList<>();
}