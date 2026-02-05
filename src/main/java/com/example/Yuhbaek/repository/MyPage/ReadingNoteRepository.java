package com.example.Yuhbaek.repository.MyPage;

import com.example.Yuhbaek.entity.MyPage.ReadingNote;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingNoteRepository extends JpaRepository<ReadingNote, Long> {

    /**
     * 사용자의 독서장 목록 조회 (최신순)
     */
    List<ReadingNote> findByUserOrderByCreatedAtDesc(UserEntity user);

    /**
     * 사용자의 특정 책 독서장 조회 (최신순)
     */
    List<ReadingNote> findByUserAndBookIsbnOrderByCreatedAtDesc(UserEntity user, String bookIsbn);

    /**
     * 특정 책에 대한 독서장 개수 조회
     */
    int countByUserAndBookIsbn(UserEntity user, String bookIsbn);

    /**
     * 사용자의 독서장 개수 조회
     */
    long countByUser(UserEntity user);
}