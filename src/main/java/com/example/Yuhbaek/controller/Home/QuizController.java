package com.example.Yuhbaek.controller.Home;

import com.example.Yuhbaek.dto.Home.*;
import com.example.Yuhbaek.service.Home.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "오늘의 퀴즈 API", description = "일일 OX 퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    /**
     * 오늘의 퀴즈 조회
     */
    @Operation(summary = "오늘의 퀴즈 조회",
            description = "오늘의 랜덤 OX 퀴즈를 조회합니다. 이미 풀었다면 결과를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/today")
    public ResponseEntity<?> getTodayQuiz(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId) {

        try {
            QuizResponse quiz = quizService.getTodayQuiz(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", quiz);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("퀴즈 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("퀴즈 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "퀴즈 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 퀴즈 답변 제출
     */
    @Operation(summary = "퀴즈 답변 제출",
            description = "오늘의 퀴즈에 대한 답변을 제출하고 결과를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제출 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 완료, 유효하지 않은 데이터 등)"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/submit")
    public ResponseEntity<?> submitAnswer(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId,

            @Parameter(description = "퀴즈 답변 정보", required = true)
            @Valid @RequestBody QuizAnswerRequest request) {

        try {
            QuizResponse result = quizService.submitAnswer(userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            response.put("message", result.getIsCorrect() ? "정답입니다! 🎉" : "아쉽게도 오답입니다 😢");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("퀴즈 답변 제출 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("퀴즈 답변 제출 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "퀴즈 답변 제출 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 퀴즈 통계 조회
     */
    @Operation(summary = "퀴즈 통계 조회",
            description = "사용자의 퀴즈 통계 정보를 조회합니다 (총 문제 수, 정답률, 연속 정답 일수 등)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/stats")
    public ResponseEntity<?> getQuizStats(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId) {

        try {
            QuizStatsResponse stats = quizService.getQuizStats(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", stats);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("퀴즈 통계 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("퀴즈 통계 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "퀴즈 통계 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 퀴즈 히스토리 조회
     */
    @Operation(summary = "퀴즈 히스토리 조회",
            description = "사용자가 풀었던 퀴즈 기록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/history")
    public ResponseEntity<?> getQuizHistory(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId) {

        try {
            List<QuizResponse> history = quizService.getQuizHistory(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", history);
            result.put("count", history.size());

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("퀴즈 히스토리 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("퀴즈 히스토리 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "퀴즈 히스토리 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }
}