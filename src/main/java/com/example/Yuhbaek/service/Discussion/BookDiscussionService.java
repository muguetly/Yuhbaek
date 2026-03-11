package com.example.Yuhbaek.service.Discussion;

import com.example.Yuhbaek.dto.Discussion.*;
import com.example.Yuhbaek.entity.Discussion.BookDiscussionRoom;
import com.example.Yuhbaek.entity.Discussion.DiscussionMessage;
import com.example.Yuhbaek.entity.Discussion.DiscussionParticipant;
import com.example.Yuhbaek.entity.MyPage.UserReadCompletion.CompletionType;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.Discussion.BookDiscussionRoomRepository;
import com.example.Yuhbaek.repository.Discussion.DiscussionMessageRepository;
import com.example.Yuhbaek.repository.Discussion.DiscussionParticipantRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import com.example.Yuhbaek.service.MyPage.ReadCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookDiscussionService {

    private final BookDiscussionRoomRepository discussionRoomRepository;
    private final DiscussionParticipantRepository participantRepository;
    private final DiscussionMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ReadCompletionService readCompletionService; // ✅ 완독 서비스 추가

    /**
     * 토론방 생성
     */
    @Transactional
    public BookDiscussionRoomResponse createDiscussionRoom(BookDiscussionCreateRequest request) {
        // 방장 조회
        UserEntity host = userRepository.findById(request.getHostId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 토론방 생성
        BookDiscussionRoom discussionRoom = BookDiscussionRoom.builder()
                .bookTitle(request.getBookTitle())
                .bookAuthor(request.getBookAuthor())
                .bookIsbn(request.getBookIsbn())
                .bookCover(request.getBookCover())
                .bookPublisher(request.getBookPublisher())
                .description(request.getDescription())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(1) // 방장이 자동으로 참여
                .discussionStartTime(request.getDiscussionStartTime())
                .status(BookDiscussionRoom.DiscussionStatus.WAITING)
                .host(host)
                .build();

        // ✅ 대화 규칙 설정
        discussionRoom.setDiscussionRules(request.getDiscussionRules());

        BookDiscussionRoom savedRoom = discussionRoomRepository.save(discussionRoom);

        // 방장을 참여자로 자동 추가
        DiscussionParticipant hostParticipant = DiscussionParticipant.builder()
                .discussionRoom(savedRoom)
                .user(host)
                .role(DiscussionParticipant.ParticipantRole.HOST)
                .isActive(true)
                .isReady(false)
                .build();
        participantRepository.save(hostParticipant);

        // 시스템 메시지 추가 (토론방 생성)
        DiscussionMessage systemMessage = DiscussionMessage.builder()
                .discussionRoom(savedRoom)
                .user(host)
                .type(DiscussionMessage.MessageType.SYSTEM)
                .content(host.getNickname() + "님이 토론방을 생성했습니다.")
                .build();
        messageRepository.save(systemMessage);

        // ✅ 규칙 안내 메시지 추가
        String rulesMessage = "📌 이 토론방의 규칙: " + String.join(", ", request.getDiscussionRules());
        DiscussionMessage rulesSystemMessage = DiscussionMessage.builder()
                .discussionRoom(savedRoom)
                .user(host)
                .type(DiscussionMessage.MessageType.SYSTEM)
                .content(rulesMessage)
                .build();
        messageRepository.save(rulesSystemMessage);

        log.info("토론방 생성 완료 - ID: {}, 도서: {}, 방장: {}, 규칙: {}",
                savedRoom.getId(), savedRoom.getBookTitle(), host.getUserId(), request.getDiscussionRules());

        return convertToResponse(savedRoom);
    }

    /**
     * 토론방 목록 조회 (진행 중 + 대기 중)
     */
    @Transactional(readOnly = true)
    public List<BookDiscussionRoomResponse> getActiveRooms() {
        List<BookDiscussionRoom> rooms = discussionRoomRepository.findActiveRooms();

        return rooms.stream()
                .peek(BookDiscussionRoom::updateStatus)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 진행 중인 토론방 목록만 조회
     */
    @Transactional
    public List<BookDiscussionRoomResponse> getInProgressRooms() {
        List<BookDiscussionRoom> rooms = discussionRoomRepository.findAll();

        rooms.forEach(room -> {
            room.updateStatus();
            discussionRoomRepository.save(room);
        });

        return rooms.stream()
                .filter(room -> room.getStatus() == BookDiscussionRoom.DiscussionStatus.IN_PROGRESS)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 대기 중인 토론방 목록만 조회
     */
    @Transactional(readOnly = true)
    public List<BookDiscussionRoomResponse> getWaitingRooms() {
        List<BookDiscussionRoom> rooms = discussionRoomRepository.findWaitingRooms();

        return rooms.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 참여 가능한 토론방 목록 조회 (정원이 남아있는 방)
     */
    @Transactional(readOnly = true)
    public List<BookDiscussionRoomResponse> getAvailableRooms() {
        List<BookDiscussionRoom> rooms = discussionRoomRepository.findAvailableRooms();

        return rooms.stream()
                .peek(BookDiscussionRoom::updateStatus)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 토론방 상세 조회
     */
    @Transactional(readOnly = true)
    public BookDiscussionRoomResponse getDiscussionRoom(Long roomId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        discussionRoom.updateStatus();

        return convertToResponse(discussionRoom);
    }

    /**
     * 토론방 입장
     */
    @Transactional
    public BookDiscussionRoomResponse joinDiscussionRoom(Long roomId, Long userId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        discussionRoom.updateStatus();

        if (participantRepository.findByDiscussionRoomAndUserAndIsActiveTrue(discussionRoom, user).isPresent()) {
            throw new IllegalStateException("이미 참여 중인 토론방입니다");
        }

        if (discussionRoom.isFull()) {
            throw new IllegalStateException("토론방 인원이 가득 찼습니다");
        }

        if (discussionRoom.getStatus() == BookDiscussionRoom.DiscussionStatus.FINISHED) {
            throw new IllegalStateException("종료된 토론방입니다");
        }

        DiscussionParticipant participant = DiscussionParticipant.builder()
                .discussionRoom(discussionRoom)
                .user(user)
                .role(DiscussionParticipant.ParticipantRole.MEMBER)
                .isActive(true)
                .isReady(false)
                .build();
        participantRepository.save(participant);

        discussionRoom.incrementParticipants();
        discussionRoomRepository.save(discussionRoom);

        DiscussionMessage enterMessage = DiscussionMessage.builder()
                .discussionRoom(discussionRoom)
                .user(user)
                .type(DiscussionMessage.MessageType.ENTER)
                .content(user.getNickname() + "님이 입장했습니다.")
                .build();
        messageRepository.save(enterMessage);

        log.info("토론방 입장 - 토론방 ID: {}, 사용자: {}", roomId, user.getUserId());

        return convertToResponse(discussionRoom);
    }

    /**
     * 토론방 퇴장
     */
    @Transactional
    public void leaveDiscussionRoom(Long roomId, Long userId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        DiscussionParticipant participant = participantRepository
                .findByDiscussionRoomAndUserAndIsActiveTrue(discussionRoom, user)
                .orElseThrow(() -> new IllegalStateException("참여하지 않은 토론방입니다"));

        participant.leave();
        participantRepository.save(participant);

        discussionRoom.decrementParticipants();
        discussionRoomRepository.save(discussionRoom);

        DiscussionMessage leaveMessage = DiscussionMessage.builder()
                .discussionRoom(discussionRoom)
                .user(user)
                .type(DiscussionMessage.MessageType.LEAVE)
                .content(user.getNickname() + "님이 퇴장했습니다.")
                .build();
        messageRepository.save(leaveMessage);

        log.info("토론방 퇴장 - 토론방 ID: {}, 사용자: {}", roomId, user.getUserId());
    }

    /**
     * 토론방 삭제 (방장만 가능)
     */
    @Transactional
    public void deleteDiscussionRoom(Long roomId, Long userId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        if (!discussionRoom.getHost().getId().equals(userId)) {
            throw new IllegalStateException("방장만 토론방을 삭제할 수 있습니다");
        }

        discussionRoomRepository.delete(discussionRoom);

        log.info("토론방 삭제 완료 - ID: {}", roomId);
    }

    /**
     * 내가 참여 중인 토론방 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BookDiscussionRoomResponse> getMyRooms(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        List<DiscussionParticipant> participants = participantRepository.findByUserAndIsActiveTrue(user);

        return participants.stream()
                .map(DiscussionParticipant::getDiscussionRoom)
                .peek(BookDiscussionRoom::updateStatus)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 토론방 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<DiscussionMessageDto> getDiscussionMessages(Long roomId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        List<DiscussionMessage> messages = messageRepository.findByDiscussionRoomOrderByCreatedAtAsc(discussionRoom);

        return messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
    }

    /**
     * 준비 상태 토글
     */
    @Transactional
    public ParticipantReadyResponse toggleReady(Long roomId, Long userId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (discussionRoom.getStatus() == BookDiscussionRoom.DiscussionStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 시작된 토론방입니다");
        }

        DiscussionParticipant participant = participantRepository
                .findByDiscussionRoomAndUserAndIsActiveTrue(discussionRoom, user)
                .orElseThrow(() -> new IllegalStateException("참여하지 않은 토론방입니다"));

        participant.toggleReady();
        participantRepository.save(participant);

        log.info("준비 상태 변경 - 토론방 ID: {}, 사용자: {}, 준비: {}",
                roomId, user.getUserId(), participant.getIsReady());

        String readyMessage = participant.getIsReady()
                ? user.getNickname() + "님이 준비 완료했습니다."
                : user.getNickname() + "님이 준비를 취소했습니다.";

        DiscussionMessage systemMessage = DiscussionMessage.builder()
                .discussionRoom(discussionRoom)
                .user(user)
                .type(DiscussionMessage.MessageType.SYSTEM)
                .content(readyMessage)
                .build();
        DiscussionMessage savedSystemMessage = messageRepository.save(systemMessage);

        DiscussionMessageDto messageDto = convertToMessageDto(savedSystemMessage);
        messagingTemplate.convertAndSend("/topic/discussion/" + roomId, messageDto);

        if (discussionRoom.areAllParticipantsReady()) {
            discussionRoom.updateStatus();
            discussionRoomRepository.save(discussionRoom);

            DiscussionMessage startMessage = DiscussionMessage.builder()
                    .discussionRoom(discussionRoom)
                    .user(user)
                    .type(DiscussionMessage.MessageType.SYSTEM)
                    .content("모든 참여자가 준비되었습니다. 토론을 시작합니다!")
                    .build();
            DiscussionMessage savedStartMessage = messageRepository.save(startMessage);

            DiscussionMessageDto startMessageDto = convertToMessageDto(savedStartMessage);
            messagingTemplate.convertAndSend("/topic/discussion/" + roomId, startMessageDto);

            log.info("모든 참여자 준비 완료 - 토론 자동 시작: {}", roomId);
        }

        return ParticipantReadyResponse.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .isReady(participant.getIsReady())
                .allReady(discussionRoom.areAllParticipantsReady())
                .roomStatus(discussionRoom.getStatus().name())
                .build();
    }

    /**
     * 강제 시작 (방장 권한)
     */
    @Transactional
    public BookDiscussionRoomResponse forceStart(Long roomId, Long userId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        if (!discussionRoom.getHost().getId().equals(userId)) {
            throw new IllegalStateException("방장만 강제 시작할 수 있습니다");
        }

        if (discussionRoom.getStatus() == BookDiscussionRoom.DiscussionStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 시작된 토론방입니다");
        }

        discussionRoom.forceStart();
        discussionRoomRepository.save(discussionRoom);

        DiscussionMessage startMessage = DiscussionMessage.builder()
                .discussionRoom(discussionRoom)
                .user(discussionRoom.getHost())
                .type(DiscussionMessage.MessageType.SYSTEM)
                .content("방장이 토론을 시작했습니다!")
                .build();
        DiscussionMessage savedStartMessage = messageRepository.save(startMessage);

        DiscussionMessageDto messageDto = convertToMessageDto(savedStartMessage);
        messagingTemplate.convertAndSend("/topic/discussion/" + roomId, messageDto);

        log.info("강제 시작 - 토론방 ID: {}, 방장: {}", roomId, discussionRoom.getHost().getUserId());

        return convertToResponse(discussionRoom);
    }

    /**
     * 참여자 목록 및 준비 상태 조회
     */
    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipants(Long roomId) {
        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        List<DiscussionParticipant> participants =
                participantRepository.findByDiscussionRoomAndIsActiveTrue(discussionRoom);

        return participants.stream()
                .map(participant -> ParticipantDto.builder()
                        .userId(participant.getUser().getId())
                        .nickname(participant.getUser().getNickname())
                        .role(participant.getRole().name())
                        .isReady(participant.getIsReady())
                        .joinedAt(participant.getJoinedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * ✅ 토론방 종료 (방장만 가능) + 참여자 전원 완독 처리
     */
    @Transactional
    public void finishDiscussionRoom(Long roomId, Long userId) {

        BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

        // 방장 확인
        if (!discussionRoom.getHost().getId().equals(userId)) {
            throw new IllegalStateException("방장만 토론방을 종료할 수 있습니다");
        }

        // 이미 종료된 방 확인
        if (discussionRoom.getStatus() == BookDiscussionRoom.DiscussionStatus.FINISHED) {
            throw new IllegalStateException("이미 종료된 토론방입니다");
        }

        // 토론방 종료
        discussionRoom.finish();
        discussionRoomRepository.save(discussionRoom);

        // 종료 시스템 메시지
        DiscussionMessage finishMessage = DiscussionMessage.builder()
                .discussionRoom(discussionRoom)
                .user(discussionRoom.getHost())
                .type(DiscussionMessage.MessageType.SYSTEM)
                .content("토론이 종료되었습니다.")
                .build();
        messageRepository.save(finishMessage);

        // WebSocket 브로드캐스트
        DiscussionMessageDto messageDto = convertToMessageDto(finishMessage);
        messagingTemplate.convertAndSend("/topic/discussion/" + roomId, messageDto);

        // 활성 참여자 전원 완독 처리 (ISBN 있을 때만)
        if (discussionRoom.getBookIsbn() != null && !discussionRoom.getBookIsbn().isBlank()) {
            List<DiscussionParticipant> participants =
                    participantRepository.findByDiscussionRoomAndIsActiveTrue(discussionRoom);

            participants.forEach(participant -> {
                readCompletionService.markAsCompleted(
                        participant.getUser().getId(),
                        discussionRoom.getBookIsbn(),
                        discussionRoom.getBookTitle(),
                        discussionRoom.getBookAuthor(),
                        discussionRoom.getBookCover(),
                        CompletionType.GROUP_CHAT
                );
            });

            log.info("토론방 종료 + 완독 처리 완료 - roomId: {}, 참여자 수: {}", roomId, participants.size());
        } else {
            log.warn("토론방에 ISBN 없음 - 완독 처리 생략. roomId: {}", roomId);
        }
    }

    /**
     * Entity -> Response 변환
     */
    private BookDiscussionRoomResponse convertToResponse(BookDiscussionRoom discussionRoom) {
        return BookDiscussionRoomResponse.builder()
                .id(discussionRoom.getId())
                .bookTitle(discussionRoom.getBookTitle())
                .bookAuthor(discussionRoom.getBookAuthor())
                .bookIsbn(discussionRoom.getBookIsbn())
                .bookCover(discussionRoom.getBookCover())
                .bookPublisher(discussionRoom.getBookPublisher())
                .description(discussionRoom.getDescription())
                .maxParticipants(discussionRoom.getMaxParticipants())
                .currentParticipants(discussionRoom.getCurrentParticipants())
                .discussionStartTime(discussionRoom.getDiscussionStartTime())
                .status(discussionRoom.getStatus().name())
                .discussionRules(discussionRoom.getDiscussionRulesList())
                .host(BookDiscussionRoomResponse.HostInfo.builder()
                        .id(discussionRoom.getHost().getId())
                        .userId(discussionRoom.getHost().getUserId())
                        .nickname(discussionRoom.getHost().getNickname())
                        .build())
                .createdAt(discussionRoom.getCreatedAt())
                .updatedAt(discussionRoom.getUpdatedAt())
                .build();
    }

    /**
     * DiscussionMessage -> DTO 변환
     */
    private DiscussionMessageDto convertToMessageDto(DiscussionMessage message) {
        return DiscussionMessageDto.builder()
                .id(message.getId())
                .discussionRoomId(message.getDiscussionRoom().getId())
                .type(message.getType().name())
                .content(message.getContent())
                .sender(DiscussionMessageDto.SenderInfo.builder()
                        .id(message.getUser().getId())
                        .userId(message.getUser().getUserId())
                        .nickname(message.getUser().getNickname())
                        .build())
                .createdAt(message.getCreatedAt())
                .build();
    }
}