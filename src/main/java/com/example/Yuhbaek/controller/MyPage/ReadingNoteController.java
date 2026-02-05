package com.example.Yuhbaek.controller.MyPage;

import com.example.Yuhbaek.dto.MyPage.ReadingNoteCreateRequest;
import com.example.Yuhbaek.dto.MyPage.ReadingNoteResponse;
import com.example.Yuhbaek.dto.MyPage.ReadingNoteUpdateRequest;
import com.example.Yuhbaek.service.MyPage.ReadingNoteService;
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
@RequestMapping("/api/mypage/reading-notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "독서장 API", description = "마이페이지 - 독서장 관련 API")
public class ReadingNoteController {

    private final ReadingNoteService readingNoteService;

    /**
     * 독서장 작성 (중복 허용)
     */
    @Operation(summary = "독서장 작성",
            description = "책을 선택하고 기억에 남는 문구를 작성합니다. 같은 책에 대해 여러 개의 독서장을 작성할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "독서장 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<?> createReadingNote(@Valid @RequestBody ReadingNoteCreateRequest request) {
        try {
            ReadingNoteResponse response = readingNoteService.createReadingNote(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "독서장이 작성되었습니다");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 작성 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 작성 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 작성 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 독서장 목록 조회
     */
    @Operation(summary = "독서장 목록 조회",
            description = "사용자의 독서장 목록을 최신순으로 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getReadingNotes(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long userId) {
        try {
            List<ReadingNoteResponse> notes = readingNoteService.getReadingNotes(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", notes.size());
            result.put("data", notes);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 목록 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 목록 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 목록 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 특정 책에 대한 독서장 목록 조회 (새로 추가)
     */
    @Operation(summary = "특정 책의 독서장 목록 조회",
            description = "특정 책에 대해 작성된 모든 독서장을 최신순으로 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/book")
    public ResponseEntity<?> getReadingNotesByBook(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "책 ISBN", required = true)
            @RequestParam String bookIsbn) {
        try {
            List<ReadingNoteResponse> notes = readingNoteService.getReadingNotesByBook(userId, bookIsbn);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", notes.size());
            result.put("data", notes);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 목록 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 목록 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 목록 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 독서장 상세 조회
     */
    @Operation(summary = "독서장 상세 조회",
            description = "특정 독서장의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "독서장을 찾을 수 없음")
    })
    @GetMapping("/detail/{noteId}")
    public ResponseEntity<?> getReadingNote(
            @Parameter(description = "독서장 ID", required = true)
            @PathVariable Long noteId) {
        try {
            ReadingNoteResponse response = readingNoteService.getReadingNote(noteId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 독서장 수정
     */
    @Operation(summary = "독서장 수정",
            description = "독서장의 기억에 남는 문구를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "독서장을 찾을 수 없음")
    })
    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateReadingNote(
            @Parameter(description = "독서장 ID", required = true)
            @PathVariable Long noteId,

            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Valid @RequestBody ReadingNoteUpdateRequest request) {
        try {
            ReadingNoteResponse response = readingNoteService.updateReadingNote(noteId, userId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "독서장이 수정되었습니다");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 수정 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 수정 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 수정 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 독서장 삭제
     */
    @Operation(summary = "독서장 삭제",
            description = "독서장을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "독서장을 찾을 수 없음")
    })
    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteReadingNote(
            @Parameter(description = "독서장 ID", required = true)
            @PathVariable Long noteId,

            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            readingNoteService.deleteReadingNote(noteId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "독서장이 삭제되었습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 삭제 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 삭제 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 삭제 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 특정 책에 대한 독서장 개수 확인 (수정됨)
     */
    @Operation(summary = "특정 책의 독서장 개수 확인",
            description = "특정 책에 대해 작성된 독서장의 개수를 확인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/count")
    public ResponseEntity<?> countReadingNotes(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "책 ISBN", required = true)
            @RequestParam String bookIsbn) {
        try {
            int count = readingNoteService.countReadingNotesByBook(userId, bookIsbn);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", count);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("독서장 개수 확인 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("독서장 개수 확인 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "독서장 개수 확인 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }
}