package com.example.Yuhbaek.controller.Discussion;

import com.example.Yuhbaek.dto.Discussion.*;
import com.example.Yuhbaek.service.Discussion.BookDiscussionService;
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
@RequestMapping("/api/book-discussions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "도서 토론방 API", description = "도서 토론방 관련 API")
public class BookDiscussionController {

    private final BookDiscussionService discussionService;

    /**
     * 토론방 생성
     */
    @Operation(summary = "토론방 생성",
            description = "도서를 선택하여 토론방을 생성합니다. 방장이 자동으로 참여자로 추가됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<?> createDiscussionRoom(
            @Valid @RequestBody BookDiscussionCreateRequest request) {

        try {
            BookDiscussionRoomResponse response = discussionService.createDiscussionRoom(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "토론방이 생성되었습니다");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("토론방 생성 실패 (잘못된 요청): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("토론방 생성 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 생성 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 모든 활성 토론방 조회 (진행 중 + 대기 중)
     */
    @Operation(summary = "활성 토론방 목록 조회",
            description = "현재 진행 중이거나 대기 중인 모든 토론방을 조회합니다")
    @GetMapping
    public ResponseEntity<?> getActiveRooms() {
        try {
            List<BookDiscussionRoomResponse> rooms = discussionService.getActiveRooms();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", rooms.size());
            result.put("data", rooms);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("토론방 목록 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 목록 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 진행 중인 토론방만 조회
     */
    @Operation(summary = "진행 중인 토론방 조회",
            description = "현재 토론이 진행 중인 방만 조회합니다")
    @GetMapping("/in-progress")
    public ResponseEntity<?> getInProgressRooms() {
        try {
            List<BookDiscussionRoomResponse> rooms = discussionService.getInProgressRooms();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", rooms.size());
            result.put("data", rooms);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("진행 중인 토론방 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 대기 중인 토론방만 조회
     */
    @Operation(summary = "대기 중인 토론방 조회",
            description = "시작 시간 전인 대기 중인 방만 조회합니다")
    @GetMapping("/waiting")
    public ResponseEntity<?> getWaitingRooms() {
        try {
            List<BookDiscussionRoomResponse> rooms = discussionService.getWaitingRooms();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", rooms.size());
            result.put("data", rooms);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("대기 중인 토론방 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 참여 가능한 토론방 조회
     */
    @Operation(summary = "참여 가능한 토론방 조회",
            description = "정원이 남아있는 참여 가능한 토론방을 조회합니다")
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRooms() {
        try {
            List<BookDiscussionRoomResponse> rooms = discussionService.getAvailableRooms();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", rooms.size());
            result.put("data", rooms);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("참여 가능한 토론방 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 토론방 상세 조회
     */
    @Operation(summary = "토론방 상세 조회",
            description = "특정 토론방의 상세 정보를 조회합니다")
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getDiscussionRoom(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId) {

        try {
            BookDiscussionRoomResponse response = discussionService.getDiscussionRoom(roomId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("토론방 조회 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (Exception e) {
            log.error("토론방 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 토론방 입장
     */
    @Operation(summary = "토론방 입장",
            description = "토론방에 입장합니다")
    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> joinDiscussionRoom(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam Long userId) {

        try {
            BookDiscussionRoomResponse response = discussionService.joinDiscussionRoom(roomId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "토론방에 입장했습니다");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("토론방 입장 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (IllegalStateException e) {
            log.error("토론방 입장 실패 (상태 오류): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("토론방 입장 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 입장 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 토론방 퇴장
     */
    @Operation(summary = "토론방 퇴장",
            description = "토론방에서 퇴장합니다")
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<?> leaveDiscussionRoom(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam Long userId) {

        try {
            discussionService.leaveDiscussionRoom(roomId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "토론방에서 퇴장했습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("토론방 퇴장 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (IllegalStateException e) {
            log.error("토론방 퇴장 실패 (상태 오류): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("토론방 퇴장 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 퇴장 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 토론방 삭제
     */
    @Operation(summary = "토론방 삭제 (방장만 가능)",
            description = "토론방을 삭제합니다. 방장만 가능합니다.")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteDiscussionRoom(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "사용자 ID (방장)", example = "1")
            @RequestParam Long userId) {

        try {
            discussionService.deleteDiscussionRoom(roomId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "토론방이 삭제되었습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("토론방 삭제 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (IllegalStateException e) {
            log.error("토론방 삭제 실패 (권한 오류): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(403).body(result);

        } catch (Exception e) {
            log.error("토론방 삭제 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 삭제 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 내가 참여 중인 토론방 조회
     */
    @Operation(summary = "내가 참여 중인 토론방 조회",
            description = "현재 사용자가 참여 중인 토론방 목록을 조회합니다")
    @GetMapping("/my-rooms")
    public ResponseEntity<?> getMyRooms(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam Long userId) {

        try {
            List<BookDiscussionRoomResponse> rooms = discussionService.getMyRooms(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", rooms.size());
            result.put("data", rooms);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("내 토론방 조회 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (Exception e) {
            log.error("내 토론방 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론방 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 토론방 메시지 조회
     */
    @Operation(summary = "토론방 메시지 조회",
            description = "토론방의 모든 메시지를 조회합니다")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getDiscussionMessages(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId) {

        try {
            List<DiscussionMessageDto> messages = discussionService.getDiscussionMessages(roomId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", messages.size());
            result.put("data", messages);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("메시지 조회 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (Exception e) {
            log.error("메시지 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "메시지 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * ✅ 준비 상태 토글
     */
    @Operation(summary = "준비 상태 토글",
            description = "참여자의 준비 상태를 변경합니다. 모든 참여자가 준비되면 토론이 시작됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "준비 상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "토론방 또는 사용자를 찾을 수 없음")
    })
    @PostMapping("/{roomId}/ready")
    public ResponseEntity<?> toggleReady(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam Long userId) {

        try {
            ParticipantReadyResponse response = discussionService.toggleReady(roomId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", response.getIsReady() ? "준비 완료" : "준비 취소");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("준비 상태 변경 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (IllegalStateException e) {
            log.error("준비 상태 변경 실패 (상태 오류): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("준비 상태 변경 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "준비 상태 변경 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * ✅ 강제 시작 (방장 권한)
     */
    @Operation(summary = "토론 강제 시작 (방장만 가능)",
            description = "방장이 준비 여부와 관계없이 토론을 강제로 시작합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "시작 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "토론방을 찾을 수 없음")
    })
    @PostMapping("/{roomId}/force-start")
    public ResponseEntity<?> forceStart(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId,

            @Parameter(description = "사용자 ID (방장)", example = "1")
            @RequestParam Long userId) {

        try {
            BookDiscussionRoomResponse response = discussionService.forceStart(roomId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "토론이 시작되었습니다");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("강제 시작 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (IllegalStateException e) {
            log.error("강제 시작 실패 (권한 오류): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(403).body(result);

        } catch (Exception e) {
            log.error("강제 시작 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "토론 시작 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * ✅ 토론방 참여자 목록 및 준비 상태 조회
     */
    @Operation(summary = "참여자 목록 및 준비 상태 조회",
            description = "토론방의 모든 참여자와 각자의 준비 상태를 조회합니다")
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<?> getParticipants(
            @Parameter(description = "토론방 ID", example = "1")
            @PathVariable Long roomId) {

        try {
            List<ParticipantDto> participants = discussionService.getParticipants(roomId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", participants.size());
            result.put("data", participants);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("참여자 조회 실패 (not found): {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.status(404).body(result);

        } catch (Exception e) {
            log.error("참여자 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "참여자 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }
}