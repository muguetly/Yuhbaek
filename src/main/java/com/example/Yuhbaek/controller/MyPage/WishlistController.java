package com.example.Yuhbaek.controller.MyPage;

import com.example.Yuhbaek.dto.MyPage.WishlistAddRequest;
import com.example.Yuhbaek.dto.MyPage.WishlistResponse;
import com.example.Yuhbaek.service.MyPage.WishlistService;
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
@RequestMapping("/api/mypage/wishlist")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "책 찜 API", description = "마이페이지 - 책 찜 관련 API")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 책 찜하기
     */
    @Operation(summary = "책 찜하기",
            description = "관심 있는 책을 찜 목록에 추가합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 찜한 책"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<?> addToWishlist(@Valid @RequestBody WishlistAddRequest request) {
        try {
            WishlistResponse response = wishlistService.addToWishlist(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "책을 찜 목록에 추가했습니다");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("책 찜 추가 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("책 찜 추가 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "책 찜 추가 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 찜 목록 조회
     */
    @Operation(summary = "찜 목록 조회",
            description = "사용자의 찜 목록을 최신순으로 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getWishlist(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long userId) {
        try {
            List<WishlistResponse> wishlists = wishlistService.getWishlist(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", wishlists.size());
            result.put("data", wishlists);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("찜 목록 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("찜 목록 조회 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "찜 목록 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 찜 취소하기 (찜 ID로)
     */
    @Operation(summary = "찜 취소하기 (ID)",
            description = "찜 ID로 찜 목록에서 제거합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "찜 정보를 찾을 수 없음")
    })
    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<?> removeFromWishlist(
            @Parameter(description = "찜 ID", required = true)
            @PathVariable Long wishlistId,

            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            wishlistService.removeFromWishlist(wishlistId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "찜 목록에서 제거했습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("찜 취소 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("찜 취소 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "찜 취소 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 찜 취소하기 (ISBN으로)
     */
    @Operation(summary = "찜 취소하기 (ISBN)",
            description = "책 ISBN으로 찜 목록에서 제거합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "찜 정보를 찾을 수 없음")
    })
    @DeleteMapping("/isbn/{bookIsbn}")
    public ResponseEntity<?> removeFromWishlistByIsbn(
            @Parameter(description = "책 ISBN", required = true)
            @PathVariable String bookIsbn,

            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            wishlistService.removeFromWishlistByIsbn(userId, bookIsbn);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "찜 목록에서 제거했습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("찜 취소 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("찜 취소 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "찜 취소 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 찜 여부 확인
     */
    @Operation(summary = "찜 여부 확인",
            description = "특정 책이 찜 목록에 있는지 확인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/check")
    public ResponseEntity<?> checkWishlist(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "책 ISBN", required = true)
            @RequestParam String bookIsbn) {
        try {
            boolean isWishlisted = wishlistService.isWishlisted(userId, bookIsbn);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("isWishlisted", isWishlisted);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("찜 여부 확인 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("찜 여부 확인 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "찜 여부 확인 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }
}