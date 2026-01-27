package com.example.Yuhbaek.controller.Discussion;

import com.example.Yuhbaek.dto.Discussion.DiscussionMessageDto;
import com.example.Yuhbaek.entity.Discussion.BookDiscussionRoom;
import com.example.Yuhbaek.entity.Discussion.DiscussionMessage;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.Discussion.BookDiscussionRoomRepository;
import com.example.Yuhbaek.repository.Discussion.DiscussionMessageRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookDiscussionWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final DiscussionMessageRepository messageRepository;
    private final BookDiscussionRoomRepository discussionRoomRepository;
    private final UserRepository userRepository;

    /**
     * 토론 메시지 전송
     * 클라이언트가 /app/discussion/{roomId}로 메시지를 보내면 이 메서드가 실행됩니다.
     */
    @MessageMapping("/discussion/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload DiscussionMessageDto messageDto) {

        try {
            // 토론방 조회
            BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

            // 사용자 조회
            UserEntity user = userRepository.findById(messageDto.getSender().getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            // 메시지 저장
            DiscussionMessage message = DiscussionMessage.builder()
                    .discussionRoom(discussionRoom)
                    .user(user)
                    .type(DiscussionMessage.MessageType.valueOf(messageDto.getType()))
                    .content(messageDto.getContent())
                    .build();

            DiscussionMessage savedMessage = messageRepository.save(message);

            // 저장된 메시지를 DTO로 변환
            DiscussionMessageDto responseDto = DiscussionMessageDto.builder()
                    .id(savedMessage.getId())
                    .discussionRoomId(roomId)
                    .type(savedMessage.getType().name())
                    .content(savedMessage.getContent())
                    .sender(DiscussionMessageDto.SenderInfo.builder()
                            .id(user.getId())
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .build())
                    .createdAt(savedMessage.getCreatedAt())
                    .build();

            // 해당 토론방을 구독하고 있는 모든 클라이언트에게 메시지 전송
            messagingTemplate.convertAndSend("/topic/discussion/" + roomId, responseDto);

            log.info("토론 메시지 전송 완료 - 토론방: {}, 사용자: {}", roomId, user.getUserId());

        } catch (Exception e) {
            log.error("토론 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 입장 메시지 전송
     */
    @MessageMapping("/discussion/{roomId}/enter")
    public void enterDiscussionRoom(
            @DestinationVariable Long roomId,
            @Payload DiscussionMessageDto messageDto) {

        try {
            BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

            UserEntity user = userRepository.findById(messageDto.getSender().getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            // 입장 메시지 저장
            DiscussionMessage message = DiscussionMessage.builder()
                    .discussionRoom(discussionRoom)
                    .user(user)
                    .type(DiscussionMessage.MessageType.ENTER)
                    .content(user.getNickname() + "님이 입장했습니다.")
                    .build();

            DiscussionMessage savedMessage = messageRepository.save(message);

            DiscussionMessageDto responseDto = DiscussionMessageDto.builder()
                    .id(savedMessage.getId())
                    .discussionRoomId(roomId)
                    .type(savedMessage.getType().name())
                    .content(savedMessage.getContent())
                    .sender(DiscussionMessageDto.SenderInfo.builder()
                            .id(user.getId())
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .build())
                    .createdAt(savedMessage.getCreatedAt())
                    .build();

            messagingTemplate.convertAndSend("/topic/discussion/" + roomId, responseDto);

            log.info("입장 메시지 전송 - 토론방: {}, 사용자: {}", roomId, user.getUserId());

        } catch (Exception e) {
            log.error("입장 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 퇴장 메시지 전송
     */
    @MessageMapping("/discussion/{roomId}/leave")
    public void leaveDiscussionRoom(
            @DestinationVariable Long roomId,
            @Payload DiscussionMessageDto messageDto) {

        try {
            BookDiscussionRoom discussionRoom = discussionRoomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("토론방을 찾을 수 없습니다"));

            UserEntity user = userRepository.findById(messageDto.getSender().getId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            // 퇴장 메시지 저장
            DiscussionMessage message = DiscussionMessage.builder()
                    .discussionRoom(discussionRoom)
                    .user(user)
                    .type(DiscussionMessage.MessageType.LEAVE)
                    .content(user.getNickname() + "님이 퇴장했습니다.")
                    .build();

            DiscussionMessage savedMessage = messageRepository.save(message);

            DiscussionMessageDto responseDto = DiscussionMessageDto.builder()
                    .id(savedMessage.getId())
                    .discussionRoomId(roomId)
                    .type(savedMessage.getType().name())
                    .content(savedMessage.getContent())
                    .sender(DiscussionMessageDto.SenderInfo.builder()
                            .id(user.getId())
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .build())
                    .createdAt(savedMessage.getCreatedAt())
                    .build();

            messagingTemplate.convertAndSend("/topic/discussion/" + roomId, responseDto);

            log.info("퇴장 메시지 전송 - 토론방: {}, 사용자: {}", roomId, user.getUserId());

        } catch (Exception e) {
            log.error("퇴장 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }
}