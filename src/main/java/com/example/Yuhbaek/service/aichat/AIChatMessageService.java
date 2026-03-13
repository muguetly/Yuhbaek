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

    private static final String BASE_PROMPT = """
너는 사용자의 '독서 친구'다.
사용자가 책에 대해 편하게 말하도록 돕되, 대화를 과하게 주도하지 않는다.
모르는 책이면 아는 척하지 말고, 오직 사용자가 말한 내용만 바탕으로 반응한다.
과한 존댓말, 설교, 지식자랑, 정답 제시는 하지 않는다.

[공통 원칙]
- 답변은 1~3문장으로 짧게 한다.
- 목록, 번호, JSON 같은 딱딱한 형식은 쓰지 않는다.
- 사용자의 말을 조금 받아주되, 그대로 반복만 하지 말고 대화가 자연스럽게 앞으로 가게 한다.
- 같은 공감 표현만 반복하지 않는다.
- 사용자가 이미 말한 내용을 길게 다시 풀어쓰지 않는다.

[대화 방식]
- 사용자가 줄거리나 상황을 설명하는 중이면 질문으로 끊지 말고 짧게 반응한다.
- 사용자가 감상이나 해석을 말하면 공감하고, 너의 짧은 생각을 덧붙인다.
- 질문은 매번 하지 말고, 정말 다음 생각으로 넘어갈 타이밍일 때만 한다.
- 네가 직전에 질문했고 사용자가 그에 답하는 흐름이면, 바로 또 질문하지 말고 1~2턴 정도는 같은 주제로 토론처럼 이어간다.
- 새로운 질문은 지금 주제가 어느 정도 정리됐거나, 사용자가 핵심 문장·장면·의견을 던졌을 때만 자연스럽게 꺼낸다.
- 질문은 너무 추상적이지 말고, 방금 나온 포인트를 붙잡아 깊게 간다.

[종료]
- 사용자가 "이제 그만", "여기까지", "끝낼래"처럼 대화를 마치려 하면 질문 없이 짧게 마무리하고 끝낸다.
""";

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

    private boolean isOpinion(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.isEmpty()) return false;

        String[] keys = {
                "생각", "느꼈", "같아", "것 같아", "의미", "상징", "나는", "내가",
                "좋았", "싫었", "재밌", "별로", "인상", "감동", "공감", "아쉬",
                "이해", "해석", "의문", "메시지", "느낌", "배려", "다정", "관계",
                "핵심", "문장", "구절", "대사", "장면", "마음에 남", "여운",
                "슬펐", "씁쓸", "인상 깊", "와닿", "먹먹", "울컥", "답답"
        };

        for (String k : keys) {
            if (t.contains(k)) return true;
        }
        return false;
    }

    private boolean isStopSignal(String text) {
        if (text == null) return false;
        String t = text.trim();

        String[] stopSignals = {
                "그만할래", "이제 그만", "여기까지", "끝낼래", "끝낼게",
                "더 말 안 할래", "더 할 말 없어", "이만할래", "이 책 얘기는 여기까지",
                "그만 얘기", "이제 됐어", "여기서 끝", "대화 끝", "이만 얘기할래"
        };

        for (String s : stopSignals) {
            if (t.contains(s)) return true;
        }
        return false;
    }

    private boolean looksLikeDiscussionTopic(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.isEmpty()) return false;

        String[] signals = {
                "어떻게 생각", "네 생각", "니 생각", "넌 어떻게", "너는 어떻게",
                "문장", "구절", "인용", "대사", "장면", "이 부분", "이 문장",
                "마음에 남", "인상 깊", "와닿", "여운", "슬펐", "답답했", "먹먹",
                "의미", "상징", "메시지", "핵심"
        };

        for (String s : signals) {
            if (t.contains(s)) return true;
        }

        return t.contains("\"") || t.contains("“") || t.contains("”") || t.contains("'");
    }

    private boolean containsQuestion(String text) {
        if (text == null) return false;
        return text.contains("?");
    }

    private List<AIChatMessage> sortAsc(List<AIChatMessage> list) {
        return list.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .toList();
    }

    private int countRecentAssistantQuestions(List<AIChatMessage> messages, int limit) {
        int count = 0;
        int checked = 0;

        for (int i = messages.size() - 1; i >= 0 && checked < limit; i--) {
            AIChatMessage m = messages.get(i);
            if (m.getRole() == MessageRole.AI) {
                checked++;
                if (containsQuestion(m.getContent())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 최근 AI 응답 중 질문 이후 얼마나 지났는지 계산
     * 0이면 방금 직전 AI가 질문
     * 1이면 질문 후 AI 응답 1개 지난 상태
     * -1이면 최근 질문 없음
     */
    private int turnsSinceLastAssistantQuestion(List<AIChatMessage> messages) {
        int assistantCountSince = 0;

        for (int i = messages.size() - 1; i >= 0; i--) {
            AIChatMessage m = messages.get(i);
            if (m.getRole() == MessageRole.AI) {
                if (containsQuestion(m.getContent())) {
                    return assistantCountSince;
                }
                assistantCountSince++;
            }
        }
        return -1;
    }

    /**
     * 질문을 던져도 되는지 최종 판단
     */
    private boolean canAskQuestionNow(List<AIChatMessage> sortedRecent, String userText) {
        int sinceLastQuestion = turnsSinceLastAssistantQuestion(sortedRecent);
        int recentQuestionCount = countRecentAssistantQuestions(sortedRecent, 3);

        if (isStopSignal(userText)) {
            return false;
        }

        if (sinceLastQuestion == 0) {
            return false;
        }

        if (sinceLastQuestion == 1) {
            return false;
        }

        if (recentQuestionCount >= 2) {
            return false;
        }

        return true;
    }

    private String buildFallbackDiscussionQuestion(String userText) {
        String t = userText == null ? "" : userText.trim();

        if (t.contains("문장") || t.contains("구절") || t.contains("대사")
                || t.contains("부분") || t.contains("장면")) {
            return "그 부분에서 너는 결국 어떤 감정이나 태도가 가장 핵심이라고 느꼈어?";
        }

        if (t.contains("슬펐") || t.contains("답답") || t.contains("씁쓸")
                || t.contains("여운") || t.contains("먹먹") || t.contains("울컥")) {
            return "그 감정이 특히 크게 남은 이유는 인물 때문이었어, 아니면 상황 때문이었어?";
        }

        if (t.contains("배려") || t.contains("다정") || t.contains("관계")) {
            return "너는 그걸 성격보다 선택이나 태도에 더 가깝다고 보는 편이야?";
        }

        if (t.contains("의미") || t.contains("상징") || t.contains("메시지")) {
            return "너는 그 부분이 결국 어떤 메시지를 가장 강하게 남긴다고 느꼈어?";
        }

        return "그 장면에서 네가 제일 중요하게 본 포인트는 뭐였어?";
    }

    private String buildClosingMessage(String userText) {
        String t = userText == null ? "" : userText.trim();

        if (t.contains("좋았") || t.contains("재밌")) {
            return "응, 그 감상 잘 전해졌어. 오늘 얘기는 여기서 마무리하자.";
        }

        if (t.contains("슬펐") || t.contains("답답") || t.contains("먹먹") || t.contains("울컥")) {
            return "응, 그 여운이 꽤 크게 남은 책이었던 것 같아. 오늘은 여기까지 하자.";
        }

        return "응, 여기까지 들을게. 오늘 대화는 이쯤에서 마무리하자.";
    }

    /**
     * 질문을 막아야 하는 턴인데 모델이 또 질문했을 때 최소 방어
     */
    private String buildFollowUpDiscussionReply(String userText) {
        String t = userText == null ? "" : userText.trim();

        if (t.contains("배려") || t.contains("다정") || t.contains("관계")) {
            return "그렇게 본 이유가 되게 납득돼. 나도 그 부분은 타고난 성격보다 관계 안에서 반복해서 드러나는 태도에 더 가깝게 느껴졌어.";
        }

        if (t.contains("슬펐") || t.contains("먹먹") || t.contains("울컥") || t.contains("답답")) {
            return "그 감정이 그렇게 남았다는 게 이해돼. 나도 그 부분은 분위기보다 인물의 선택이 더 크게 남는 지점처럼 느껴졌어.";
        }

        if (t.contains("의미") || t.contains("상징") || t.contains("메시지")) {
            return "그렇게 읽은 해석이 자연스럽게 들려. 나도 그 부분은 이야기 전체를 묶어 주는 핵심에 가깝다고 느껴졌어.";
        }

        return "그렇게 받아들인 게 되게 자연스럽게 들려. 나도 네가 짚은 지점이 이 이야기에서 꽤 핵심이라고 느껴졌어.";
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

        // 1) 사용자 메시지 저장
        AIChatMessage userMsg = new AIChatMessage();
        userMsg.setRoomId(roomId);
        userMsg.setRole(MessageRole.USER);
        userMsg.setContent(content);
        messageRepository.save(userMsg);

        // 2) 최근 메시지 불러오기
        List<AIChatMessage> recent = messageRepository.findByRoomIdOrderByCreatedAtDesc(
                roomId,
                PageRequest.of(0, 12)
        );

        List<AIChatMessage> sortedRecent = sortAsc(recent);

        List<Map<String, String>> chat = sortedRecent.stream()
                .map(m -> Map.of(
                        "role", m.getRole() == MessageRole.USER ? "user" : "assistant",
                        "content", m.getContent()
                ))
                .toList();

        boolean stopSignal = isStopSignal(content);
        boolean opinion = isOpinion(content);
        boolean topicCandidate = looksLikeDiscussionTopic(content);
        boolean allowQuestion = canAskQuestionNow(sortedRecent, content);

        String modeHint;
        boolean mustAskThisTurn = false;
        boolean forceNoQuestion = false;

        if (stopSignal) {
            modeHint = """
[CLOSING]
질문 없이 짧게 마무리하고 끝내라.
""";
            forceNoQuestion = true;

        } else if (!allowQuestion && (opinion || topicCandidate)) {
            modeHint = """
[FOLLOW]
바로 새 질문하지 말고, 사용자의 말에 공감하고 네 생각을 짧게 보태며 토론처럼 이어가라.
""";
            forceNoQuestion = true;

        } else if (topicCandidate) {
            modeHint = """
[DISCUSS]
공감 1문장, 네 생각 1문장, 마지막에 짧은 질문 1문장으로 이어가라.
""";
            mustAskThisTurn = true;

        } else if (opinion) {
            modeHint = """
[OPINION]
공감하고 네 생각을 짧게 보태라. 질문은 없어도 된다.
""";

        } else {
            modeHint = """
[STORY]
질문으로 끊지 말고 짧게 반응하며 계속 말할 수 있게 하라.
""";
        }

        String systemPrompt = BASE_PROMPT + "\n" + modeHint;

        // 3) 모델 호출
        String aiText = openAIClient.generateWithSystem(systemPrompt, chat);

        // 4) 종료 신호면 무조건 질문 제거
        if (stopSignal) {
            if (aiText == null || aiText.isBlank() || containsQuestion(aiText)) {
                aiText = buildClosingMessage(content);
            }
        }

        // 5) 이번 턴 질문 금지인데 질문이 나오면 토론형 문장으로 교체
        if (forceNoQuestion && aiText != null && containsQuestion(aiText)) {
            aiText = stopSignal
                    ? buildClosingMessage(content)
                    : buildFollowUpDiscussionReply(content);
        }

        // 6) 이번 턴은 질문해야 하는데 질문이 없으면 보정
        if (mustAskThisTurn && (aiText == null || !containsQuestion(aiText))) {
            String base = (aiText == null ? "" : aiText.trim());

            if (!base.isEmpty()
                    && !base.endsWith(".")
                    && !base.endsWith("!")
                    && !base.endsWith("?")) {
                base += ".";
            }

            if (!base.isEmpty()) {
                base += " ";
            }

            aiText = base + buildFallbackDiscussionQuestion(content);
        }

        // 7) 비어 있으면 최소 방어
        if (aiText == null || aiText.isBlank()) {
            if (stopSignal) {
                aiText = buildClosingMessage(content);
            } else if (mustAskThisTurn) {
                aiText = "그 부분이 그냥 지나가는 장면은 아닌 것 같아. " + buildFallbackDiscussionQuestion(content);
            } else if (forceNoQuestion) {
                aiText = buildFollowUpDiscussionReply(content);
            } else if (opinion) {
                aiText = "그렇게 느낀 지점이 되게 중요하게 들려. 나도 그 부분은 꽤 오래 남는 감상일 것 같아.";
            } else {
                aiText = "응, 그 흐름으로 이해돼. 이어서 더 말해줘.";
            }
        }

        // 8) 저장
        AIChatMessage aiMsg = new AIChatMessage();
        aiMsg.setRoomId(roomId);
        aiMsg.setRole(MessageRole.AI);
        aiMsg.setContent(aiText);
        aiMsg = messageRepository.save(aiMsg);

        room.setLastMessageAt(LocalDateTime.now());

        return MessageResponse.builder()
                .messageId(aiMsg.getMessageId())
                .role(aiMsg.getRole())
                .content(aiMsg.getContent())
                .createdAt(aiMsg.getCreatedAt())
                .build();
    }
}