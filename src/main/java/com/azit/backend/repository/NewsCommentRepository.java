package com.azit.backend.repository;

import com.azit.backend.entity.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
}