package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.dto.aichat.CreateRoomRequest;
import com.example.Yuhbaek.dto.aichat.RoomListItemResponse;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import com.example.Yuhbaek.service.aichat.AIChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aichat/rooms")
@RequiredArgsConstructor
@Tag(name = "AI 채팅방 API", description = "책별 AI 채팅방 생성/조회/완독 처리 API")
public class AIChatRoomController {

    private final AIChatRoomService roomService;

    @Operation(summary = "AI 채팅방 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Long> createRoom(
            @RequestHeader("X-USER-ID") @NotNull Long userId,
            @Valid @RequestBody CreateRoomRequest request
    ) {
        Long roomId = roomService.createOrGetRoom(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
    }

    @Operation(summary = "AI 채팅방 목록 조회")
    @GetMapping
    public ResponseEntity<List<RoomListItemResponse>> getRooms(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam(required = false) RoomStatus status
    ) {
        return ResponseEntity.ok(roomService.listRooms(userId, status));
    }

    @Operation(summary = "AI 채팅방 완독 처리")
    @PatchMapping("/{roomId}/finish")
    public ResponseEntity<Void> finishRoom(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long roomId
    ) {
        roomService.finishRoom(userId, roomId);
        return ResponseEntity.noContent().build();
    }
}