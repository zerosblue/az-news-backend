package com.azit.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileImg;

    // ★ 여기가 핵심! (절대 String이나 javax.management.relation.Role 이면 안 됨)
    // 같은 패키지(entity) 안에 있는 Role Enum을 쓴다는 뜻이야.
    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;
    private String providerId;
    private String interests;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastLogin;
}