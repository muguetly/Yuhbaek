package com.example.Yuhbaek.repository.Discussion;

import com.example.Yuhbaek.entity.Discussion.BookDiscussionRoom;
import com.example.Yuhbaek.entity.Discussion.DiscussionMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscussionMessageRepository extends JpaRepository<DiscussionMessage, Long> {

    /**
     * 특정 토론방의 메시지 조회 (시간순)
     */
    List<DiscussionMessage> findByDiscussionRoomOrderByCreatedAtAsc(BookDiscussionRoom discussionRoom);

    /**
     * 특정 토론방의 최근 메시지 N개 조회
     */
    @Query("SELECT m FROM DiscussionMessage m WHERE m.discussionRoom = :discussionRoom " +
            "ORDER BY m.createdAt DESC")
    List<DiscussionMessage> findRecentMessages(BookDiscussionRoom discussionRoom);

    /**
     * 특정 시간 이후의 메시지 조회
     */
    List<DiscussionMessage> findByDiscussionRoomAndCreatedAtAfterOrderByCreatedAtAsc(
            BookDiscussionRoom discussionRoom,
            LocalDateTime after
    );

    /**
     * 특정 토론방의 메시지 개수 조회
     */
    Long countByDiscussionRoom(BookDiscussionRoom discussionRoom);
}