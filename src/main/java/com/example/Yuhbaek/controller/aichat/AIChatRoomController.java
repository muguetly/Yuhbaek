package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.controller.common.SessionAuthSupport;
import com.example.Yuhbaek.dto.aichat.CreateRoomRequest;
import com.example.Yuhbaek.dto.aichat.RoomListItemResponse;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import com.example.Yuhbaek.service.aichat.AIChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
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
@Tag(name = "AI 채팅방 API", description = "책별 AI 채팅방 생성/조회/완독 처리 API")
public class AIChatRoomController extends SessionAuthSupport {

    private final AIChatRoomService roomService;

    @Operation(summary = "AI 채팅방 생성")
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
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "roomId", roomId));
        } catch (IllegalStateException e) {
            return unauthorized();
        }
    }

    @Operation(summary = "AI 채팅방 목록 조회")
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

    // ⚠️ 주의: 완독은 세션의 finish-session을 쓰는 게 주 흐름이라면,
    // 이 API는 "상태만 바꾸는 용도"로 남겨두거나(관리용),
    // 프론트에서 사용하지 않도록 하면 됨.
    @Operation(summary = "AI 채팅방 완독 처리(상태만 변경)")
    @PatchMapping("/{roomId}/finish")
    public ResponseEntity<?> finishRoom(
            @PathVariable Long roomId,
            HttpSession session
    ) {
        try {
            Long userId = requireLogin(session);
            roomService.finishRoom(userId, roomId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return unauthorized();
        }
    }
}
