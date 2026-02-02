package com.example.Yuhbaek.service.aichat;

import com.example.Yuhbaek.dto.aichat.MessageResponse;
import com.example.Yuhbaek.entity.aichat.AIChatMessage;
import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import com.example.Yuhbaek.entity.aichat.MessageRole;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import com.example.Yuhbaek.repository.aichat.AIChatMessageRepository;
import com.example.Yuhbaek.repository.aichat.AIChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIChatMessageService {

    private final AIChatRoomRepository roomRepository;
    private final AIChatMessageRepository messageRepository;
    private final OpenAIClient openAIClient;

    // ✅ "끊지 말고 기다리기" 중심 BASE 프롬프트 (항상 공통 적용)
    private static final String BASE_PROMPT = """
너는 사용자의 '독서 친구'야. 사용자가 말하는 흐름을 끊지 말고, 먼저 충분히 말하게 해줘.
과한 존댓말/설교/해설/지식자랑 금지. 사용자가 말한 내용만 기반으로 반응해.
모르는 책이면 아는 척하지 말고 "사용자가 말한 내용"만 붙잡고 대화해.

[가장 중요한 규칙]
- 사용자가 아직 줄거리/상황/사실을 말하는 중이면, 질문으로 대화를 빼앗지 마.
  -> 이때는 '짧은 반응 + 계속 말하게 하는 한마디'만 해. (질문 금지에 가깝게)
  예: "오 거기까지 봤구나. 계속 말해줘" / "헐 그 장면 인상적이지" / "좋다, 더 얘기해줘"

- 사용자가 '생각/해석/감상/평가'를 말했을 때만:
  1) 공감 1~2문장
  2) AI의 생각 1문장(짧게, 단정 금지)
  3) 발제문을 자연스럽게 1개(라벨 없이 문장 속에 섞기)
  를 한다.

[질문 규칙]
- 매 메시지마다 질문으로 끝내지 마.
- 질문을 하더라도 2~3턴에 한 번 정도만.
- 초반/줄거리 단계에서는 "어떻게 생각해?" 같은 질문 금지. 대신 "응응 계속 말해줘" 같은 유도만.

[출력]
- 1~3문장. 목록/번호/JSON/라벨/설명문 금지.
""";

    /**
     * ✅ 간단 휴리스틱: 사용자가 '생각/감상/평가'를 말했는지 감지
     * - 완벽할 필요 없음. "질문으로 끊는 느낌"을 줄이는 게 목적.
     */
    private boolean isOpinion(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.isEmpty()) return false;

        // 감상/평가/해석 신호들
        String[] keys = {
                "생각", "느꼈", "같아", "의미", "상징", "왜", "나는", "내가",
                "답답", "좋았", "싫었", "재밌", "별로", "인상", "감동", "공감",
                "아쉬", "좋아", "싫어", "이해", "해석", "의문"
        };

        for (String k : keys) {
            if (t.contains(k)) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long userId, Long roomId, int size) {

        // 본인 방인지 체크
        roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        int pageSize = Math.min(Math.max(size, 1), 100);

        // 최신순으로 조회
        List<AIChatMessage> list = messageRepository.findByRoomIdOrderByCreatedAtDesc(
                roomId,
                PageRequest.of(0, pageSize)
        );

        // 과거 → 현재 순으로 정렬해서 반환
        return list.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(m -> MessageResponse.builder()
                        .messageId(m.getMessageId())
                        .role(m.getRole())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
    }

    /**
     * USER 저장 + AI 생성/저장까지 한 번에
     */
    @Transactional
    public MessageResponse sendUserMessage(Long userId, Long roomId, String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is required");
        }

        // 본인 방인지 체크
        AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        // 완독 방이면 막기
        if (RoomStatus.FINISHED.equals(room.getStatus())) {
            throw new IllegalStateException("finished room: cannot send message");
        }

        // 1) USER 저장
        AIChatMessage userMsg = new AIChatMessage();
        userMsg.setRoomId(roomId);
        userMsg.setRole(MessageRole.USER);
        userMsg.setContent(content);
        messageRepository.save(userMsg);

        // 2) 최근 메시지 N개 로드 (USER/AI 모두)
        List<AIChatMessage> recent = messageRepository.findByRoomIdOrderByCreatedAtDesc(
                roomId,
                PageRequest.of(0, 20)
        );

        List<Map<String, String>> chat = recent.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(m -> Map.of(
                        "role", m.getRole() == MessageRole.USER ? "user" : "assistant",
                        "content", m.getContent()
                ))
                .toList();

        // ✅ 3) 모드 힌트(줄거리 단계 vs 감상 단계) + BASE 프롬프트 결합
        boolean opinion = isOpinion(content);



        String modeHint = opinion
                ? """
                  사용자가 지금 '생각/해석/감상/평가'를 말했어.
                  - 공감 1~2문장 + 너의 짧은 생각 1문장 + 발제문을 자연스럽게 1개(라벨 없이) 해도 돼.
                  """
                : """
                  사용자가 아직 줄거리/상황/사실을 말하는 중이야.
                  - 질문하지 말고(특히 '어떻게 생각해?' 금지) 짧게 반응만 해.
                  - 마지막은 '계속 말해줘/더 얘기해줘' 같은 한마디로만 이어가.
                  - 발제문은 지금은 만들지 마.
                  """;

        String systemPrompt = BASE_PROMPT + "\n\n[현재 모드 지시]\n" + modeHint;

        // 4) OpenAI 호출
        String aiText = openAIClient.generateWithSystem(systemPrompt, chat);

        // 5) AI 저장
        AIChatMessage aiMsg = new AIChatMessage();
        aiMsg.setRoomId(roomId);
        aiMsg.setRole(MessageRole.AI);
        aiMsg.setContent(aiText);
        aiMsg = messageRepository.save(aiMsg);

        // 6) lastMessageAt 업데이트
        room.setLastMessageAt(LocalDateTime.now());
        // roomRepository.save(room); // 더티체킹이면 생략 가능

        // 7) 반환: “AI 메시지”를 반환 (프론트에서 바로 보여주기 편함)
        return MessageResponse.builder()
                .messageId(aiMsg.getMessageId())
                .role(aiMsg.getRole())
                .content(aiMsg.getContent())
                .createdAt(aiMsg.getCreatedAt())
                .build();
    }
}