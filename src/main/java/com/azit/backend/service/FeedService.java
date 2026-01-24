package com.azit.backend.service;

import com.azit.backend.dto.FeedDto;
import com.azit.backend.entity.*;
import com.azit.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final MemberRepository memberRepository;
    private final FeedHeartRepository feedHeartRepository;
    private final FollowRepository followRepository;
    private final FeedImageRepository feedImageRepository;
    private final FeedCommentRepository feedCommentRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    // 1. 피드 작성
    @Transactional
    public void createFeed(String email, FeedDto.Request request, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        Feed feed = Feed.builder()
                .content(request.getContent())
                .member(member)
                .isPublic(true)
                .build();
        saveImages(files, feed);
        feedRepository.save(feed);
    }

    // 2. 피드 목록
    @Transactional(readOnly = true)
    public List<FeedDto.Response> getFeeds(String email, String type) {
        Member me = (email != null) ? memberRepository.findByEmail(email).orElse(null) : null;
        List<Feed> feeds;

        if ("following".equals(type) && me != null) {
            List<Member> followings = followRepository.findByFollower(me).stream()
                    .map(Follow::getFollowing).collect(Collectors.toList());
            followings.add(me);
            feeds = feedRepository.findByMemberInOrderByCreatedAtDesc(followings);
        } else {
            feeds = feedRepository.findAllByOrderByCreatedAtDesc();
        }

        return feeds.stream().map(feed -> {
            boolean isHearted = (me != null) && feedHeartRepository.existsByMemberAndFeed(me, feed);
            boolean isFollowed = false;
            if (me != null && !me.getId().equals(feed.getMember().getId())) {
                isFollowed = followRepository.existsByFollowerAndFollowing(me, feed.getMember());
            }
            return new FeedDto.Response(feed, isHearted, isFollowed);
        }).collect(Collectors.toList());
    }

    // 3. 좋아요
    @Transactional
    public boolean toggleHeart(Long feedId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        Feed feed = feedRepository.findById(feedId).orElseThrow();
        var existingHeart = feedHeartRepository.findByMemberAndFeed(member, feed);
        if (existingHeart.isPresent()) {
            feedHeartRepository.delete(existingHeart.get()); return false;
        } else {
            feedHeartRepository.save(FeedHeart.builder().member(member).feed(feed).build()); return true;
        }
    }

    // 4. 피드 삭제
    @Transactional
    public void deleteFeed(Long feedId, String email) {
        Feed feed = feedRepository.findById(feedId).orElseThrow();
        if(!feed.getMember().getEmail().equals(email)) throw new RuntimeException("권한 없음");
        feedRepository.delete(feed);
    }

    // 5. 피드 수정
    @Transactional
    public void updateFeed(Long feedId, String email, FeedDto.Request request, List<MultipartFile> files) throws IOException {
        Feed feed = feedRepository.findById(feedId).orElseThrow();
        if (!feed.getMember().getEmail().equals(email)) throw new RuntimeException("권한 없음");
        feed.update(request.getContent());
        saveImages(files, feed);
    }

    // 6. 이미지 개별 삭제
    @Transactional
    public void deleteFeedImage(Long imageId, String email) {
        FeedImage image = feedImageRepository.findById(imageId).orElseThrow();
        if (!image.getFeed().getMember().getEmail().equals(email)) throw new RuntimeException("권한 없음");
        feedImageRepository.delete(image);
    }

    // 7. 팔로우 토글
    @Transactional
    public boolean toggleFollow(String followerEmail, String targetEmail) {
        Member follower = memberRepository.findByEmail(followerEmail).orElseThrow();
        Member following = memberRepository.findByEmail(targetEmail).orElseThrow();
        if (follower.equals(following)) throw new RuntimeException("Self follow not allowed");
        var existingFollow = followRepository.findByFollowerAndFollowing(follower, following);
        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get()); return false;
        } else {
            followRepository.save(Follow.builder().follower(follower).following(following).build()); return true;
        }
    }

    // 8. 댓글 작성
    @Transactional
    public void writeComment(Long feedId, String email, String content, Long parentId) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        Feed feed = feedRepository.findById(feedId).orElseThrow();
        FeedComment parent = null;
        if (parentId != null) parent = feedCommentRepository.findById(parentId).orElseThrow();
        feedCommentRepository.save(FeedComment.builder().content(content).member(member).feed(feed).parent(parent).build());
    }

    // 9. 팔로워 목록
    @Transactional(readOnly = true)
    public List<FeedDto.FollowResponse> getFollowers(String myEmail) {
        Member me = memberRepository.findByEmail(myEmail).orElseThrow();
        List<Follow> follows = followRepository.findByFollowing(me);
        return follows.stream().map(f -> {
            Member fan = f.getFollower();
            boolean isFollowedByMe = followRepository.existsByFollowerAndFollowing(me, fan);
            return new FeedDto.FollowResponse(fan, isFollowedByMe);
        }).collect(Collectors.toList());
    }

    // 10. 팔로잉 목록
    @Transactional(readOnly = true)
    public List<FeedDto.FollowResponse> getFollowings(String myEmail) {
        Member me = memberRepository.findByEmail(myEmail).orElseThrow();
        List<Follow> follows = followRepository.findByFollower(me);
        return follows.stream().map(f -> {
            Member star = f.getFollowing();
            return new FeedDto.FollowResponse(star, true);
        }).collect(Collectors.toList());
    }

    // ★★★ [여기 추가됨!] 11. 댓글 수정
    @Transactional
    public void updateComment(Long commentId, String email, String newContent) {
        FeedComment comment = feedCommentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글 없음"));
        if (!comment.getMember().getEmail().equals(email)) throw new RuntimeException("권한 없음");
        comment.update(newContent);
    }

    // ★★★ [여기 추가됨!] 12. 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String email) {
        FeedComment comment = feedCommentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글 없음"));
        if (!comment.getMember().getEmail().equals(email)) throw new RuntimeException("권한 없음");
        feedCommentRepository.delete(comment);
    }

    private void saveImages(List<MultipartFile> files, Feed feed) throws IOException {
        if (files != null && !files.isEmpty()) {
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();
            for (MultipartFile file : files) {
                String saveName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                file.transferTo(new File(uploadDir + saveName));
                FeedImage img = FeedImage.builder().imgUrl("/images/" + saveName).build();
                img.setFeed(feed);
            }
        }
    }
    // 13. 리트윗 (공유하기)
    @Transactional
    public void retweet(Long feedId, String email) {
        Member me = memberRepository.findByEmail(email).orElseThrow();
        Feed targetFeed = feedRepository.findById(feedId).orElseThrow();

        // 이미 리트윗한 글인지 확인 (중복 방지 - 선택사항)
        // 여기선 쿨하게 중복 허용하거나, 간단히 로직 추가 가능

        // 원본이 리트윗된 글이라면, 그 원본의 원본을 가져옴 (트위터 방식)
        if (targetFeed.getOriginalFeed() != null) {
            targetFeed = targetFeed.getOriginalFeed();
        }

        Feed retweet = Feed.builder()
                .content(null) // 내용 없음 (단순 리트윗)
                .member(me)
                .originalFeed(targetFeed) // ★ 원본 연결
                .isPublic(true)
                .build();

        feedRepository.save(retweet);
    }
}