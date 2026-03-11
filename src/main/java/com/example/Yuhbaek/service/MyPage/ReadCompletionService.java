package com.example.Yuhbaek.service.MyPage;

import com.example.Yuhbaek.dto.MyPage.ReadCompletionResponse;
import com.example.Yuhbaek.entity.MyPage.UserReadCompletion;
import com.example.Yuhbaek.entity.MyPage.UserReadCompletion.CompletionType;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.MyPage.UserReadCompletionRepository;
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
public class ReadCompletionService {

    private final UserReadCompletionRepository completionRepository;
    private final UserRepository userRepository;

    /**
     * 완독 처리 (중복이면 무시)
     *
     * @param userId         완독한 유저 ID
     * @param bookIsbn       책 ISBN
     * @param bookTitle      책 제목
     * @param bookAuthor     책 저자
     * @param bookCover      책 표지 URL
     * @param completionType 완독 경로 (AI_CHAT / GROUP_CHAT)
     */
    @Transactional
    public void markAsCompleted(Long userId, String bookIsbn, String bookTitle,
                                String bookAuthor, String bookCover,
                                CompletionType completionType) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 이미 완독한 책이면 무시 (중복 방지)
        if (completionRepository.existsByUserAndBookIsbn(user, bookIsbn)) {
            log.info("이미 완독한 책 - userId: {}, isbn: {}", userId, bookIsbn);
            return;
        }

        UserReadCompletion completion = UserReadCompletion.builder()
                .user(user)
                .bookIsbn(bookIsbn)
                .bookTitle(bookTitle)
                .bookAuthor(bookAuthor)
                .bookCover(bookCover)
                .completionType(completionType)
                .build();

        completionRepository.save(completion);

        log.info("완독 처리 완료 - userId: {}, isbn: {}, type: {}", userId, bookIsbn, completionType);
    }

    /**
     * 내 완독 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReadCompletionResponse> getMyCompletions(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return completionRepository.findByUserOrderByCompletedAtDesc(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 책 완독 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isCompleted(Long userId, String bookIsbn) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return completionRepository.existsByUserAndBookIsbn(user, bookIsbn);
    }

    private ReadCompletionResponse convertToResponse(UserReadCompletion completion) {
        return ReadCompletionResponse.builder()
                .id(completion.getId())
                .bookIsbn(completion.getBookIsbn())
                .bookTitle(completion.getBookTitle())
                .bookAuthor(completion.getBookAuthor())
                .bookCover(completion.getBookCover())
                .completionType(completion.getCompletionType())
                .completedAt(completion.getCompletedAt())
                .build();
    }
}