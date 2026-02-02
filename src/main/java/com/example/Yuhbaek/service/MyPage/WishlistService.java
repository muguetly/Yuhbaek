package com.example.Yuhbaek.service.MyPage;

import com.example.Yuhbaek.dto.MyPage.WishlistAddRequest;
import com.example.Yuhbaek.dto.MyPage.WishlistResponse;
import com.example.Yuhbaek.entity.MyPage.BookWishlist;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.MyPage.BookWishlistRepository;
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
public class WishlistService {

    private final BookWishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    /**
     * 책 찜하기
     */
    @Transactional
    public WishlistResponse addToWishlist(WishlistAddRequest request) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. 이미 찜한 책인지 확인
        if (wishlistRepository.existsByUserAndBookIsbn(user, request.getBookIsbn())) {
            throw new IllegalArgumentException("이미 찜한 책입니다");
        }

        // 3. 찜 추가
        BookWishlist wishlist = BookWishlist.builder()
                .user(user)
                .bookIsbn(request.getBookIsbn())
                .bookTitle(request.getBookTitle())
                .author(request.getAuthor())
                .coverImage(request.getCoverImage())
                .publisher(request.getPublisher())
                .build();

        BookWishlist savedWishlist = wishlistRepository.save(wishlist);
        log.info("책 찜 추가 - 사용자: {}, 책: {}", user.getUserId(), request.getBookTitle());

        return convertToResponse(savedWishlist);
    }

    /**
     * 찜 취소하기
     */
    @Transactional
    public void removeFromWishlist(Long wishlistId, Long userId) {
        // 1. 찜 정보 조회
        BookWishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new IllegalArgumentException("찜 정보를 찾을 수 없습니다"));

        // 2. 본인의 찜인지 확인
        if (!wishlist.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 찜만 삭제할 수 있습니다");
        }

        // 3. 삭제
        wishlistRepository.delete(wishlist);
        log.info("책 찜 취소 - 사용자: {}, 책: {}", wishlist.getUser().getUserId(), wishlist.getBookTitle());
    }

    /**
     * ISBN으로 찜 취소하기
     */
    @Transactional
    public void removeFromWishlistByIsbn(Long userId, String bookIsbn) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. 찜 정보 조회
        BookWishlist wishlist = wishlistRepository.findByUserAndBookIsbn(user, bookIsbn)
                .orElseThrow(() -> new IllegalArgumentException("찜 정보를 찾을 수 없습니다"));

        // 3. 삭제
        wishlistRepository.delete(wishlist);
        log.info("책 찜 취소 - 사용자: {}, ISBN: {}", user.getUserId(), bookIsbn);
    }

    /**
     * 찜 목록 조회
     */
    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(Long userId) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. 찜 목록 조회
        List<BookWishlist> wishlists = wishlistRepository.findByUserOrderByCreatedAtDesc(user);

        // 3. 응답 변환
        return wishlists.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 찜 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isWishlisted(Long userId, String bookIsbn) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return wishlistRepository.existsByUserAndBookIsbn(user, bookIsbn);
    }

    /**
     * Entity -> Response 변환
     */
    private WishlistResponse convertToResponse(BookWishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .bookIsbn(wishlist.getBookIsbn())
                .bookTitle(wishlist.getBookTitle())
                .author(wishlist.getAuthor())
                .coverImage(wishlist.getCoverImage())
                .publisher(wishlist.getPublisher())
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}