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

    /**
     * 핵심:
     * - 줄거리 단계에서는 끊지 않는다.
     * - 사용자가 감상을 말하면 공감 + 짧은 의견.
     * - 사용자가 생각을 어느 정도 마쳤거나, AI 의견을 물었거나,
     *   핵심 문장/인용을 던졌으면 반드시 발제 질문으로 깊게 간다.
     */
    private static final String BASE_PROMPT = """
너는 사용자의 '독서 친구'다.
사용자가 책에 대해 편하게 말하도록 돕되, 대화를 과하게 주도하지 않는다.
과한 존댓말, 설교, 요약 강박, 지식자랑, 정답 제시를 하지 않는다.
모르는 책이면 아는 척하지 말고, 오직 사용자가 말한 내용만 기반으로 반응한다.

[공통 원칙]
- 답변은 1~3문장.
- 목록/번호/JSON/라벨 금지.
- 사용자의 표현을 조금 받아주되, 그대로 반복만 하지 말고 대화가 한 단계 앞으로 가게 해라.
- "좋다", "공감돼" 같은 말만 반복하지 말 것.
- 사용자가 이미 충분히 말한 뒤에는 공감만 반복하지 말고 다음 생각으로 넘어가게 도와라.

[가장 중요한 규칙]
1) 사용자가 아직 줄거리/상황/사실을 설명하는 단계면:
- 질문으로 끊지 마라.
- 짧게 반응하고 계속 말할 수 있게만 해라.
- 발제 질문을 억지로 만들지 마라.

2) 사용자가 생각/해석/감상/평가를 말하는 단계면:
- 공감 1문장
- AI의 짧은 생각 1문장
- 필요하면 대화를 깊게 하는 발제 질문 1문장
으로 반응하라.

3) 사용자가 아래 중 하나에 해당하면, 반드시 발제 질문을 1개 던져라.
- 자기 생각을 어느 정도 마무리했다.
- 핵심 문장/인용문을 꺼냈다.
- "넌 어떻게 생각해?"처럼 AI 의견을 구했다.
- "난 더 할 말 없어"처럼 공을 넘겼다.
- 같은 결의 공감만 2턴 이상 반복될 것 같다.

[발제 질문 규칙]
- 발제 질문은 반드시 질문형 문장이어야 한다.
- "어떻게 생각해?"로 끝나도 좋다.
- 너무 추상적이지 말고, 방금 사용자가 말한 문장 하나를 잡고 깊게 파라.
- 예:
  - "그럼 너는 다정함이 배려를 넘어서 일종의 능력이라고도 보는 편이야?"
  - "상대를 안심시키는 태도가 결국 관계의 힘을 만든다고 느낀 거네, 그게 모든 관계에 다 통한다고 생각해?"
  - "그 문장에서 너는 다정함을 성격보다 선택에 더 가깝게 본 거야?"

[금지]
- 매번 똑같이 공감만 하기
- 사용자가 이미 해석했는데 그 해석을 그대로 길게 재진술만 하기
- 질문해야 할 타이밍인데 질문 없이 끝내기
""";

    private boolean isOpinion(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.isEmpty()) return false;

        String[] keys = {
                "생각", "느꼈", "같아", "같은", "의미", "상징", "나는", "내가",
                "답답", "좋았", "싫었", "재밌", "별로", "인상", "감동", "공감",
                "아쉬", "좋아", "싫어", "이해", "해석", "의문", "메시지", "느낌",
                "지능", "배려", "다정", "관계", "핵심", "문장", "구절"
        };

        for (String k : keys) {
            if (t.contains(k)) return true;
        }
        return false;
    }

    /**
     * 발제 질문을 "반드시" 던져야 하는 순간 감지
     */
    private boolean shouldAskDiscussionQuestion(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.isEmpty()) return false;

        String[] askSignals = {
                "어떻게 생각", "넌 어떻게", "너는 어떻게", "네 생각", "니 생각",
                "어때", "맞지", "그치", "그렇지", "라고 생각", "라고 봐",
                "난 더 이상 할 말", "더 할 말 없", "여기까지", "이 정도",
                "\"", "“", "”", "핵심", "문장", "구절", "발제", "주제"
        };

        for (String k : askSignals) {
            if (t.contains(k)) return true;
        }

        // 따옴표 인용문이 길게 들어오면 발제 가능성이 큼
        return t.length() >= 20 && (t.contains("“") || t.contains("”") || t.contains("\""));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long userId, Long roomId, int size) {
        roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        int pageSize = Math.min(Math.max(size, 1), 100);

        List<AIChatMessage> list = messageRepository.findByRoomIdOrderByCreatedAtDesc(
                roomId,
                PageRequest.of(0, pageSize)
        );

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

    @Transactional
    public MessageResponse sendUserMessage(Long userId, Long roomId, String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is required");
        }

        AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        if (RoomStatus.FINISHED.equals(room.getStatus())) {
            throw new IllegalStateException("finished room: cannot send message");
        }

        // 1) USER 저장
        AIChatMessage userMsg = new AIChatMessage();
        userMsg.setRoomId(roomId);
        userMsg.setRole(MessageRole.USER);
        userMsg.setContent(content);
        messageRepository.save(userMsg);

        // 2) 최근 메시지 로드
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

        boolean opinion = isOpinion(content);
        boolean mustAsk = shouldAskDiscussionQuestion(content);

        String modeHint;

        if (mustAsk) {
            modeHint = """
            [현재 모드: DISCUSSION]
            사용자가 자기 생각을 어느 정도 말했거나, 핵심 문장/인용/의견 요청을 던졌다.
            이번 응답에서는 반드시 아래 구조를 따른다.
            - 공감 또는 받아주기 1문장
            - 너의 짧은 생각 1문장
            - 발제 질문 1문장 (반드시 질문형)
            마지막 문장은 반드시 질문이어야 한다.
            "어떻게 생각해?" 형태 가능.
            공감만 하고 끝내면 안 된다.
            """;
        } else if (opinion) {
            modeHint = """
            [현재 모드: OPINION]
            사용자가 감상/해석/평가를 말하고 있다.
            - 공감 1문장
            - 너의 짧은 생각 1문장
            - 필요하면 발제 질문 1문장
            단, 이미 충분히 말한 흐름 같으면 질문으로 한 단계 깊게 들어가라.
            """;
        } else {
            modeHint = """
            [현재 모드: STORY]
            사용자가 아직 줄거리/상황/사실을 말하는 중이다.
            - 질문으로 끊지 마라.
            - 짧게 반응하고 계속 말하게 해라.
            - 발제 질문은 만들지 마라.
            """;
        }

        String systemPrompt = BASE_PROMPT + "\n\n" + modeHint;

        // 4) OpenAI 호출
        String aiText = openAIClient.generateWithSystem(systemPrompt, chat);

        // 5) AI 저장
        AIChatMessage aiMsg = new AIChatMessage();
        aiMsg.setRoomId(roomId);
        aiMsg.setRole(MessageRole.AI);
        aiMsg.setContent(aiText);
        aiMsg = messageRepository.save(aiMsg);

        // 6) 방 업데이트
        room.setLastMessageAt(LocalDateTime.now());

        return MessageResponse.builder()
                .messageId(aiMsg.getMessageId())
                .role(aiMsg.getRole())
                .content(aiMsg.getContent())
                .createdAt(aiMsg.getCreatedAt())
                .build();
    }
}