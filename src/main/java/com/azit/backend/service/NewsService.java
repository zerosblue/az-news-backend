package com.azit.backend.service;

import com.azit.backend.dto.NewsDto;
import com.azit.backend.entity.Member;
import com.azit.backend.entity.News;
import com.azit.backend.entity.NewsComment;
import com.azit.backend.repository.MemberRepository;
import com.azit.backend.repository.NewsCommentRepository;
import com.azit.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final NewsCommentRepository newsCommentRepository;

    // 1. 뉴스 목록 조회
    @Transactional(readOnly = true)
    public List<NewsDto.Response> getNewsList(String category) {
        List<News> newsList;
        if (category == null || category.equals("전체")) {
            newsList = newsRepository.findAllByOrderByIdDesc();
        } else {
            newsList = newsRepository.findByCategoryOrderByIdDesc(category);
        }
        return newsList.stream().map(NewsDto.Response::new).collect(Collectors.toList());
    }

    // 2. 뉴스 상세 조회 (댓글 포함)
    @Transactional(readOnly = true)
    public NewsDto.Response getNewsDetail(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("뉴스 없음"));
        return new NewsDto.Response(news);
    }

    // 3. 댓글 작성
    @Transactional
    public void writeComment(Long newsId, String email, String content) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        News news = newsRepository.findById(newsId).orElseThrow();

        NewsComment comment = NewsComment.builder()
                .content(content)
                .member(member)
                .news(news)
                .build();

        newsCommentRepository.save(comment);
    }
}