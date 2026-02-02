package com.example.Yuhbaek.repository.Discussion;

import com.example.Yuhbaek.entity.Discussion.BookDiscussionRoom;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookDiscussionRoomRepository extends JpaRepository<BookDiscussionRoom, Long> {

    /**
     * 현재 진행 중인 토론방 조회 (시작 시간이 지났고 종료되지 않은 방)
     */
    @Query("SELECT dr FROM BookDiscussionRoom dr WHERE dr.status = 'IN_PROGRESS' ORDER BY dr.createdAt DESC")
    List<BookDiscussionRoom> findInProgressRooms();

    /**
     * 대기 중인 토론방 조회 (시작 시간 전)
     */
    @Query("SELECT dr FROM BookDiscussionRoom dr WHERE dr.status = 'WAITING' ORDER BY dr.discussionStartTime ASC")
    List<BookDiscussionRoom> findWaitingRooms();

    /**
     * 진행 중 + 대기 중인 모든 활성 토론방 조회
     */
    @Query("SELECT dr FROM BookDiscussionRoom dr WHERE dr.status IN ('WAITING', 'IN_PROGRESS') ORDER BY dr.discussionStartTime ASC")
    List<BookDiscussionRoom> findActiveRooms();

    /**
     * 특정 사용자가 방장인 토론방 조회
     */
    List<BookDiscussionRoom> findByHostOrderByCreatedAtDesc(UserEntity host);

    /**
     * 도서 제목으로 토론방 검색
     */
    List<BookDiscussionRoom> findByBookTitleContainingAndStatusInOrderByCreatedAtDesc(
            String bookTitle,
            List<BookDiscussionRoom.DiscussionStatus> statuses
    );

    /**
     * 참여 가능한 방 조회 (정원이 남아있는 방)
     */
    @Query("SELECT dr FROM BookDiscussionRoom dr WHERE dr.currentParticipants < dr.maxParticipants " +
            "AND dr.status IN ('WAITING', 'IN_PROGRESS') ORDER BY dr.discussionStartTime ASC")
    List<BookDiscussionRoom> findAvailableRooms();

    /**
     * 특정 시간 이후에 시작하는 토론방 조회
     */
    List<BookDiscussionRoom> findByDiscussionStartTimeAfterAndStatusOrderByDiscussionStartTimeAsc(
            LocalDateTime startTime,
            BookDiscussionRoom.DiscussionStatus status
    );
}