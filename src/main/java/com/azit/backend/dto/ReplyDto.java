package com.azit.backend.dto;

import com.azit.backend.entity.Reply;
import lombok.Data;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ReplyDto {
    private Long id;
    private String content;
    private String writerName;
    private String createdAt;
    private List<ReplyDto> children; // ★ 내 자식 댓글들 (무한 계층의 핵심)

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.content = reply.isDeleted() ? "삭제된 댓글입니다." : reply.getContent();
        this.writerName = reply.getMember().getNickname();
        this.createdAt = reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

        // 내 자식들을 DTO로 변환해서 리스트에 담기 (재귀 호출)
        this.children = reply.getChildren().stream()
                .map(ReplyDto::new)
                .collect(Collectors.toList());
    }
}