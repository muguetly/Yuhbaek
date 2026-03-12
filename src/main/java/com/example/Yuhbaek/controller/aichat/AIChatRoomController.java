package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.controller.common.SessionAuthSupport;
import com.example.Yuhbaek.dto.aichat.CreateRoomRequest;
import com.example.Yuhbaek.dto.aichat.RoomListItemResponse;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import com.example.Yuhbaek.service.aichat.AIChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aichat/rooms")
@RequiredArgsConstructor
@Tag(name = "AI 채팅방 API", description = "AI 채팅방 생성, 목록 조회, 삭제 API")
public class AIChatRoomController extends SessionAuthSupport {

    private final AIChatRoomService roomService;

    @Operation(summary = "채팅방 생성", description = "도서와 감정 정보를 기반으로 AI 채팅방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @PostMapping
    public ResponseEntity<?> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            HttpSession session
    ) {
        try {
            Long userId = requireLogin(session);
            Long roomId = roomService.createOrGetRoom(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "roomId", roomId));
        } catch (IllegalStateException e) {
            return unauthorized();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Operation(summary = "채팅방 목록 조회", description = "사용자의 AI 채팅방 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getRooms(
            @RequestParam(required = false) RoomStatus status,
            HttpSession session
    ) {
        try {
            Long userId = requireLogin(session);
            List<RoomListItemResponse> rooms = roomService.listRooms(userId, status);
            return ResponseEntity.ok(Map.of("success", true, "data", rooms));
        } catch (IllegalStateException e) {
            return unauthorized();
        }
    }

    /**
     * 저장하지 않고 나가기
     * - 해당 방 메시지 삭제
     * - 해당 방 세션 삭제
     * - 해당 방 감정 로그 삭제
     * - 채팅방 삭제
     */
    @Operation(summary = "채팅방 삭제", description = "저장하지 않고 나갈 때 채팅방과 관련 데이터를 삭제합니다.")
    @DeleteMapping("/{roomId}/discard")
    public ResponseEntity<?> discardRoom(
            @PathVariable Long roomId,
            HttpSession session
    ) {
        try {
            Long userId = requireLogin(session);
            roomService.discardRoom(userId, roomId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return unauthorized();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}