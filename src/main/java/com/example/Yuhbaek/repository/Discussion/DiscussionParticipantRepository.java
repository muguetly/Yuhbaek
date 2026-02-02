package com.example.Yuhbaek.repository.Discussion;

import com.example.Yuhbaek.entity.Discussion.BookDiscussionRoom;
import com.example.Yuhbaek.entity.Discussion.DiscussionParticipant;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscussionParticipantRepository extends JpaRepository<DiscussionParticipant, Long> {

    /**
     * 특정 토론방의 활성 참여자 목록 조회
     */
    List<DiscussionParticipant> findByDiscussionRoomAndIsActiveTrue(BookDiscussionRoom discussionRoom);

    /**
     * 특정 사용자가 특정 토론방에 참여 중인지 확인
     */
    Optional<DiscussionParticipant> findByDiscussionRoomAndUserAndIsActiveTrue(BookDiscussionRoom discussionRoom, UserEntity user);

    /**
     * 특정 사용자가 참여 중인 모든 토론방 조회
     */
    @Query("SELECT p FROM DiscussionParticipant p WHERE p.user = :user AND p.isActive = true " +
            "ORDER BY p.joinedAt DESC")
    List<DiscussionParticipant> findByUserAndIsActiveTrue(UserEntity user);

    /**
     * 특정 토론방의 활성 참여자 수 조회
     */
    Long countByDiscussionRoomAndIsActiveTrue(BookDiscussionRoom discussionRoom);

    /**
     * 특정 토론방의 모든 참여자 조회 (퇴장한 사람 포함)
     */
    List<DiscussionParticipant> findByDiscussionRoomOrderByJoinedAtAsc(BookDiscussionRoom discussionRoom);
}