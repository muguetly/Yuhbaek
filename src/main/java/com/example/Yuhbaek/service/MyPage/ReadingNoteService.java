package com.example.Yuhbaek.service.MyPage;

import com.example.Yuhbaek.dto.MyPage.ReadingNoteCreateRequest;
import com.example.Yuhbaek.dto.MyPage.ReadingNoteResponse;
import com.example.Yuhbaek.dto.MyPage.ReadingNoteUpdateRequest;
import com.example.Yuhbaek.entity.MyPage.ReadingNote;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.MyPage.ReadingNoteRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingNoteService {

    private final ReadingNoteRepository readingNoteRepository;
    private final UserRepository userRepository;

    /**
     * 독서장 작성 (중복 허용)
     */
    @Transactional
    public ReadingNoteResponse createReadingNote(ReadingNoteCreateRequest request) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. 중복 체크 제거 - 같은 책에 대해 여러 개의 독서장 작성 가능

        // 3. 독서장 생성
        ReadingNote readingNote = ReadingNote.builder()
                .user(user)
                .bookIsbn(request.getBookIsbn())
                .bookTitle(request.getBookTitle())
                .author(request.getAuthor())
                .coverImage(request.getCoverImage())
                .publisher(request.getPublisher())
                .memorableQuote(request.getMemorableQuote())
                .build();

        ReadingNote savedNote = readingNoteRepository.save(readingNote);
        log.info("독서장 작성 - 사용자: {}, 책: {}", user.getUserId(), request.getBookTitle());

        return convertToResponse(savedNote);
    }

    /**
     * 독서장 수정 (문구만 수정)
     */
    @Transactional
    public ReadingNoteResponse updateReadingNote(Long noteId, Long userId, ReadingNoteUpdateRequest request) {
        // 1. 독서장 조회
        ReadingNote readingNote = readingNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("독서장을 찾을 수 없습니다"));

        // 2. 본인의 독서장인지 확인
        if (!readingNote.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 독서장만 수정할 수 있습니다");
        }

        // 3. 문구 수정
        readingNote.updateQuote(request.getMemorableQuote());
        log.info("독서장 수정 - 사용자: {}, 독서장 ID: {}", readingNote.getUser().getUserId(), noteId);

        return convertToResponse(readingNote);
    }

    /**
     * 독서장 삭제
     */
    @Transactional
    public void deleteReadingNote(Long noteId, Long userId) {
        // 1. 독서장 조회
        ReadingNote readingNote = readingNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("독서장을 찾을 수 없습니다"));

        // 2. 본인의 독서장인지 확인
        if (!readingNote.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 독서장만 삭제할 수 있습니다");
        }

        // 3. 삭제
        readingNoteRepository.delete(readingNote);
        log.info("독서장 삭제 - 사용자: {}, 책: {}", readingNote.getUser().getUserId(), readingNote.getBookTitle());
    }

    /**
     * 독서장 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReadingNoteResponse> getReadingNotes(Long userId) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. 독서장 목록 조회
        List<ReadingNote> notes = readingNoteRepository.findByUserOrderByCreatedAtDesc(user);

        // 3. 응답 변환
        return notes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 독서장 상세 조회
     */
    @Transactional(readOnly = true)
    public ReadingNoteResponse getReadingNote(Long noteId) {
        ReadingNote readingNote = readingNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("독서장을 찾을 수 없습니다"));

        return convertToResponse(readingNote);
    }

    /**
     * 특정 책에 대한 독서장 개수 조회 (중복 허용으로 변경)
     */
    @Transactional(readOnly = true)
    public int countReadingNotesByBook(Long userId, String bookIsbn) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return readingNoteRepository.countByUserAndBookIsbn(user, bookIsbn);
    }

    /**
     * 특정 책에 대한 독서장 목록 조회 (새로 추가)
     */
    @Transactional(readOnly = true)
    public List<ReadingNoteResponse> getReadingNotesByBook(Long userId, String bookIsbn) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        List<ReadingNote> notes = readingNoteRepository.findByUserAndBookIsbnOrderByCreatedAtDesc(user, bookIsbn);

        return notes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Entity -> Response 변환
     */
    private ReadingNoteResponse convertToResponse(ReadingNote readingNote) {
        return ReadingNoteResponse.builder()
                .id(readingNote.getId())
                .userId(readingNote.getUser().getId())
                .bookIsbn(readingNote.getBookIsbn())
                .bookTitle(readingNote.getBookTitle())
                .author(readingNote.getAuthor())
                .coverImage(readingNote.getCoverImage())
                .publisher(readingNote.getPublisher())
                .memorableQuote(readingNote.getMemorableQuote())
                .createdAt(readingNote.getCreatedAt())
                .updatedAt(readingNote.getUpdatedAt())
                .build();
    }
}