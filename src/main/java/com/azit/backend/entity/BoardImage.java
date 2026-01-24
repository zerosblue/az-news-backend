package com.azit.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board_image")
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl; // 이미지 접속 주소 (예: /images/uuid_파일이름.jpg)
    private String originalName; // 원본 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 연관관계 편의 메서드
    public void setBoard(Board board) {
        this.board = board;
        if (!board.getImages().contains(this)) {
            board.getImages().add(this);
        }
    }
}