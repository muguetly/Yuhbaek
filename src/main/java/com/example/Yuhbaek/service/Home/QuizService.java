package com.example.Yuhbaek.service.Home;

import com.example.Yuhbaek.dto.Home.*;
import com.example.Yuhbaek.entity.Home.QuizEntity;
import com.example.Yuhbaek.entity.Home.UserQuizHistory;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.Home.QuizRepository;
import com.example.Yuhbaek.repository.Home.UserQuizHistoryRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserQuizHistoryRepository quizHistoryRepository;
    private final UserRepository userRepository;

    /**
     * 오늘의 퀴즈 가져오기
     */
    @Transactional(readOnly = true)
    public QuizResponse getTodayQuiz(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        LocalDate today = LocalDate.now();

        // 오늘 이미 퀴즈를 풀었는지 확인
        if (quizHistoryRepository.existsByUserAndQuizDate(user, today)) {
            // 이미 푼 퀴즈 정보 반환
            UserQuizHistory history = quizHistoryRepository.findByUserAndQuizDate(user, today)
                    .orElseThrow(() -> new IllegalArgumentException("퀴즈 기록을 찾을 수 없습니다"));

            return QuizResponse.builder()
                    .id(history.getQuiz().getId())
                    .question(history.getQuiz().getQuestion())
                    .category(history.getQuiz().getCategory())
                    .answer(history.getQuiz().getAnswer())
                    .explanation(history.getQuiz().getExplanation())
                    .userAnswer(history.getUserAnswer())
                    .isCorrect(history.getIsCorrect())
                    .build();
        }

        // 랜덤 퀴즈 가져오기
        QuizEntity quiz = quizRepository.findRandomQuiz()
                .orElseThrow(() -> new IllegalArgumentException("사용 가능한 퀴즈가 없습니다"));

        // 답변 전이므로 정답과 해설은 제외
        return QuizResponse.builder()
                .id(quiz.getId())
                .question(quiz.getQuestion())
                .category(quiz.getCategory())
                .build();
    }

    /**
     * 퀴즈 답변 제출
     */
    @Transactional
    public QuizResponse submitAnswer(Long userId, QuizAnswerRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        QuizEntity quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("퀴즈를 찾을 수 없습니다"));

        LocalDate today = LocalDate.now();

        // 오늘 이미 퀴즈를 풀었는지 확인
        if (quizHistoryRepository.existsByUserAndQuizDate(user, today)) {
            throw new IllegalArgumentException("오늘 이미 퀴즈를 완료했습니다");
        }

        // 정답 확인
        boolean isCorrect = quiz.getAnswer().equals(request.getAnswer());

        // 퀴즈 기록 저장
        UserQuizHistory history = UserQuizHistory.builder()
                .user(user)
                .quiz(quiz)
                .userAnswer(request.getAnswer())
                .isCorrect(isCorrect)
                .quizDate(today)
                .build();

        quizHistoryRepository.save(history);

        log.info("퀴즈 답변 제출 완료 - 사용자: {}, 퀴즈ID: {}, 정답여부: {}",
                user.getUserId(), quiz.getId(), isCorrect);

        // 결과 반환 (정답, 해설 포함)
        return QuizResponse.builder()
                .id(quiz.getId())
                .question(quiz.getQuestion())
                .category(quiz.getCategory())
                .answer(quiz.getAnswer())
                .explanation(quiz.getExplanation())
                .userAnswer(request.getAnswer())
                .isCorrect(isCorrect)
                .build();
    }

    /**
     * 퀴즈 통계 조회
     */
    @Transactional(readOnly = true)
    public QuizStatsResponse getQuizStats(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 전체 퀴즈 개수와 정답 개수 조회
        Long totalQuizzes = quizHistoryRepository.countTotalQuizzes(user);
        Long correctCount = quizHistoryRepository.countCorrectAnswers(user);

        // 정답률 계산
        double correctRate = totalQuizzes > 0 ? (correctCount * 100.0 / totalQuizzes) : 0.0;

        // 연속 정답 일수 계산
        Integer currentStreak = quizHistoryRepository.calculateCurrentStreak(user.getId());
        if (currentStreak == null) {
            currentStreak = 0;
        }

        // 오늘 퀴즈 완료 여부
        boolean todayCompleted = quizHistoryRepository.existsByUserAndQuizDate(user, LocalDate.now());

        return QuizStatsResponse.builder()
                .totalQuizzes(totalQuizzes.intValue())
                .correctCount(correctCount.intValue())
                .correctRate(Math.round(correctRate * 10.0) / 10.0) // 소수점 첫째자리
                .currentStreak(currentStreak)
                .todayCompleted(todayCompleted)
                .build();
    }

    /**
     * 사용자의 퀴즈 히스토리 조회
     */
    @Transactional(readOnly = true)
    public List<QuizResponse> getQuizHistory(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        List<UserQuizHistory> histories = quizHistoryRepository.findByUserOrderByQuizDateDesc(user);

        return histories.stream()
                .map(history -> QuizResponse.builder()
                        .id(history.getQuiz().getId())
                        .question(history.getQuiz().getQuestion())
                        .category(history.getQuiz().getCategory())
                        .answer(history.getQuiz().getAnswer())
                        .explanation(history.getQuiz().getExplanation())
                        .userAnswer(history.getUserAnswer())
                        .isCorrect(history.getIsCorrect())
                        .build())
                .collect(Collectors.toList());
    }
}