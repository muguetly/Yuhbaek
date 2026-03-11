package com.example.Yuhbaek.repository.MyPage;

import com.example.Yuhbaek.entity.MyPage.UserReadCompletion;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReadCompletionRepository extends JpaRepository<UserReadCompletion, Long> {

    // 내 완독 목록 전체 (최신순)
    List<UserReadCompletion> findByUserOrderByCompletedAtDesc(UserEntity user);

    // 특정 책 완독 여부 확인
    boolean existsByUserAndBookIsbn(UserEntity user, String bookIsbn);

    // 특정 책 완독 정보 조회
    Optional<UserReadCompletion> findByUserAndBookIsbn(UserEntity user, String bookIsbn);
}