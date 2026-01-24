package com.azit.backend.repository;

import com.azit.backend.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 특별한 메소드 없어도 됨 (JPA가 알아서 함)
}