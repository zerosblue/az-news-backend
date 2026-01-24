package com.azit.backend.scheduler;

import com.azit.backend.entity.Member;
import com.azit.backend.entity.News;
import com.azit.backend.entity.Notification;
import com.azit.backend.repository.MemberRepository;
import com.azit.backend.repository.NewsRepository;
import com.azit.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeywordAlertScheduler {

    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    // 1분마다 실행
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkKeywords() {
        System.out.println("👀 [스케줄러] 키워드 매칭 시작...");

        try {
            // 1. 최신 뉴스 20개 가져오기
            List<News> recentNews = newsRepository.findAllByOrderByIdDesc().stream().limit(20).toList();
            List<Member> members = memberRepository.findAll();

            // 2. 모든 회원 돌면서 검사
            for (Member member : members) {
                String interests = member.getInterests();
                if (interests == null || interests.trim().isEmpty()) continue;

                List<String> keywords = Arrays.asList(interests.split(","));

                for (News news : recentNews) {
                    for (String keyword : keywords) {
                        String cleanKeyword = keyword.trim().replace("#", ""); // 샵 제거
                        if (cleanKeyword.isEmpty()) continue;

                        // 제목에 키워드가 포함되어 있다면?
                        if (news.getTitle().contains(cleanKeyword)) {

                            // ★ [수정됨] DB한테 직접 물어봄 (훨씬 빠르고 에러 안 남)
                            boolean alreadySent = notificationRepository.existsByMemberAndLink(member, news.getLink());

                            // 보낸 적 없으면 알림 생성
                            if (!alreadySent) {
                                createNotification(member, cleanKeyword, news);
                            }
                            // 한 뉴스에서 키워드 하나 찾았으면 다음 뉴스로 (알림 도배 방지)
                            break;
                        }
                    }
                }
            }
            System.out.println("✅ [스케줄러] 검사 완료.");

        } catch (Exception e) {
            // 에러가 나도 서버가 죽지 않게 예외 처리
            System.err.println("❌ 스케줄러 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createNotification(Member member, String keyword, News news) {
        Notification notification = Notification.builder()
                .member(member)
                .message("키워드 [" + keyword + "] 소식: " + news.getTitle())
                .link(news.getLink())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        System.out.println("🔔 알림 발송! -> " + member.getNickname() + " (" + keyword + ")");
    }
}