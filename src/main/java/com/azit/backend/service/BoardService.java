package com.azit.backend.service;

import com.azit.backend.dto.BoardDto;
import com.azit.backend.entity.Board;
import com.azit.backend.entity.BoardImage;
import com.azit.backend.entity.Member;
import com.azit.backend.entity.Reply;
import com.azit.backend.repository.BoardImageRepository; // ★ 추가됨
import com.azit.backend.repository.BoardRepository;
import com.azit.backend.repository.MemberRepository;
import com.azit.backend.repository.ReplyRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;
    private final BoardImageRepository boardImageRepository; // ★ 추가됨

    // 프로젝트 경로 (이미지 저장용)
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    // 1. 글 쓰기 (기존 동일)
    @Transactional
    public void writeBoard(String email, BoardDto.Request request, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .member(member)
                .build();

        uploadFiles(files, board); // 파일 저장 로직 분리함
        boardRepository.save(board);
    }

    // 2. 목록 조회 (기존 동일)
    @Transactional(readOnly = true)
    public List<BoardDto.Response> getBoardList(String category) {
        List<Board> boards;
        if (category == null || category.equals("전체")) {
            boards = boardRepository.findAllByOrderByCreatedAtDesc();
        } else {
            boards = boardRepository.findByCategoryOrderByCreatedAtDesc(category);
        }
        return boards.stream().map(BoardDto.Response::new).collect(Collectors.toList());
    }

    // 3. 상세 조회 (기존 동일)
    @Transactional
    public BoardDto.Response getBoardDetail(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("글 없음"));
        board.increaseViewCount();
        return new BoardDto.Response(board);
    }

    // 4. 댓글 작성 (기존 동일)
    @Transactional
    public void writeReply(Long boardId, String email, String content, Long parentId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("글 없음"));

        Reply parent = null;
        if (parentId != null) {
            parent = replyRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("부모 댓글 없음"));
        }

        Reply reply = Reply.builder()
                .member(member)
                .board(board)
                .content(content)
                .parent(parent)
                .build();
        replyRepository.save(reply);
    }

    // ★ 5. 글 수정 (사진 추가 가능하도록 변경)
    @Transactional
    public void updateBoard(Long boardId, String email, BoardDto.Request request, List<MultipartFile> files) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("글 없음"));

        if (!board.getMember().getEmail().equals(email)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        // 텍스트 정보 수정
        board.update(request.getTitle(), request.getContent(), request.getCategory());

        // 새 이미지 추가
        uploadFiles(files, board);
    }

    // ★ 6. 글 삭제 (기존 동일)
    @Transactional
    public void deleteBoard(Long boardId, String email) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("글 없음"));

        if (!board.getMember().getEmail().equals(email)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }
        boardRepository.delete(board);
    }

    // ★ 7. 이미지 개별 삭제 (수정 화면에서 X 버튼 눌렀을 때)
    @Transactional
    public void deleteImage(Long imageId, String email) {
        BoardImage image = boardImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지 없음"));

        // 본인 글의 이미지인지 확인
        if (!image.getBoard().getMember().getEmail().equals(email)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 실제 파일 삭제 (선택 사항 - 여기선 생략하고 DB만 지움)
        boardImageRepository.delete(image);
    }

    // [도우미 함수] 파일 업로드 로직 (중복 제거용)
    private void uploadFiles(List<MultipartFile> files, Board board) throws IOException {
        if (files != null && !files.isEmpty()) {
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String originalName = file.getOriginalFilename();
                String saveName = UUID.randomUUID() + "_" + originalName;
                file.transferTo(new File(uploadDir + saveName));

                BoardImage boardImage = BoardImage.builder()
                        .originalName(originalName)
                        .imgUrl("/images/" + saveName)
                        .build();
                boardImage.setBoard(board);
            }
        }
    }
}