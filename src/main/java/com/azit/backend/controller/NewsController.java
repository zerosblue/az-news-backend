package com.azit.backend.controller;

import com.azit.backend.entity.News;
import com.azit.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    // 주소: /api/news?category=주식  (없으면 전체)
    @GetMapping("/api/news")
    public List<News> getNewsList(@RequestParam(required = false) String category) {
        if (category == null || category.equals("전체")) {
            return newsRepository.findAllByOrderByIdDesc();
        }
        return newsRepository.findByCategoryOrderByIdDesc(category);
    }
}