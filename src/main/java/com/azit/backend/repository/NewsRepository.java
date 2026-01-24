package com.azit.backend.repository;

import com.azit.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    // 1. 전체 뉴스 가져오기 (최신순)
    List<News> findAllByOrderByIdDesc();

    // 2. 카테고리별 뉴스 가져오기 (예: "주식"만 최신순으로)
    List<News> findByCategoryOrderByIdDesc(String category);
}