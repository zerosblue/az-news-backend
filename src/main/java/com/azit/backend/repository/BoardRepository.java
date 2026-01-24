package com.azit.backend.repository;

import com.azit.backend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 최신순 조회
    List<Board> findAllByOrderByCreatedAtDesc();

    // 카테고리별 조회 (주식, 맛집 등)
    List<Board> findByCategoryOrderByCreatedAtDesc(String category);
}