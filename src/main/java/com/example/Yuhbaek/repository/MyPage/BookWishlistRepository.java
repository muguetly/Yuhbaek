package com.example.Yuhbaek.repository.MyPage;

import com.example.Yuhbaek.entity.MyPage.BookWishlist;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookWishlistRepository extends JpaRepository<BookWishlist, Long> {

    /**
     * 사용자의 찜 목록 조회 (최신순)
     */
    List<BookWishlist> findByUserOrderByCreatedAtDesc(UserEntity user);

    /**
     * 특정 사용자의 특정 책 찜 여부 확인
     */
    boolean existsByUserAndBookIsbn(UserEntity user, String bookIsbn);

    /**
     * 특정 사용자의 특정 책 찜 정보 조회
     */
    Optional<BookWishlist> findByUserAndBookIsbn(UserEntity user, String bookIsbn);

    /**
     * 사용자의 찜 개수 조회
     */
    long countByUser(UserEntity user);
}