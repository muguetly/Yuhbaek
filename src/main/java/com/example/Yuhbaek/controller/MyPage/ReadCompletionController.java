package com.example.Yuhbaek.controller.MyPage;

import com.example.Yuhbaek.dto.MyPage.ReadCompletionResponse;
import com.example.Yuhbaek.service.MyPage.ReadCompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/read-completions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "완독 API", description = "완독 기록 관련 API")
public class ReadCompletionController {

    private final ReadCompletionService completionService;

    @Operation(summary = "내 완독 목록 조회")
    @GetMapping
    public ResponseEntity<?> getMyCompletions(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {

        try {
            List<ReadCompletionResponse> completions = completionService.getMyCompletions(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", completions.size());
            result.put("data", completions);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "특정 책 완독 여부 확인")
    @GetMapping("/check")
    public ResponseEntity<?> checkCompletion(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "책 ISBN", required = true)
            @RequestParam String isbn) {

        try {
            boolean completed = completionService.isCompleted(userId, isbn);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("completed", completed);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}