package com.example.Yuhbaek.service.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenAIThinkingStyleClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    public Scores analyze(String userText) {
        if (userText == null) userText = "";
        if (userText.length() > 12000) userText = userText.substring(0, 12000);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);

        // Responses API input
        body.put("input", buildPrompt(userText));

        // ✅ FIX: response_format -> text.format (Responses API 최신 규격)
        Map<String, Object> schema = Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "critic", Map.of("type", "integer", "minimum", 0, "maximum", 100),
                        "emotion", Map.of("type", "integer", "minimum", 0, "maximum", 100),
                        "analysis", Map.of("type", "integer", "minimum", 0, "maximum", 100),
                        "empathy", Map.of("type", "integer", "minimum", 0, "maximum", 100),
                        "creative", Map.of("type", "integer", "minimum", 0, "maximum", 100)
                ),
                "required", List.of("critic", "emotion", "analysis", "empathy", "creative")
        );

        body.put("text", Map.of(
                "format", Map.of(
                        "type", "json_schema",
                        "name", "thinking_style_scores",
                        "schema", schema,
                        "strict", true
                )
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = restTemplate.exchange(
                baseUrl + "/responses",
                HttpMethod.POST,
                entity,
                String.class
        );
        try {
            resp = restTemplate.exchange(
                    baseUrl + "/responses",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("===== OPENAI STATUS ===== " + resp.getStatusCode());
            System.out.println("===== OPENAI RAW RESPONSE =====");
            System.out.println(resp.getBody());
            System.out.println("================================");

        } catch (org.springframework.web.client.HttpStatusCodeException he) {
            System.out.println("===== OPENAI STATUS ===== " + he.getStatusCode());
            System.out.println("===== OPENAI ERROR BODY =====");
            System.out.println(he.getResponseBodyAsString());
            System.out.println("================================");
            throw he;
        }


        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("OpenAI 응답이 비정상입니다: " + resp.getStatusCode());
        }

        try {
            JsonNode root = objectMapper.readTree(resp.getBody());

            String jsonText = null;

// 1) 혹시 output_text 필드가 직접 있으면 사용
            if (root.hasNonNull("output_text")) {
                jsonText = root.get("output_text").asText();
            }

// 2) output[*].content[*]를 전부 순회하면서 찾기
            if (jsonText == null || jsonText.isBlank()) {
                JsonNode output = root.path("output");
                if (output.isArray()) {
                    for (JsonNode out : output) {
                        JsonNode content = out.path("content");
                        if (!content.isArray()) continue;

                        for (JsonNode c : content) {
                            // content.type == "output_text" 인 경우 text에 JSON 문자열이 들어있음
                            if (c.hasNonNull("text")) {
                                String t = c.get("text").asText();
                                if (t != null && !t.isBlank()) {
                                    jsonText = t;
                                    break;
                                }
                            }
                        }
                        if (jsonText != null && !jsonText.isBlank()) break;
                    }
                }
            }

            if (jsonText == null || jsonText.isBlank()) {
                String raw = resp.getBody();
                if (raw == null) raw = "(null)";
                if (raw.length() > 4000) raw = raw.substring(0, 4000);
                throw new IllegalStateException("OpenAI JSON 결과를 찾지 못했습니다. RAW=" + raw);
            }

            JsonNode scores = objectMapper.readTree(jsonText);
            return new Scores(
                    scores.get("critic").asInt(),
                    scores.get("emotion").asInt(),
                    scores.get("analysis").asInt(),
                    scores.get("empathy").asInt(),
                    scores.get("creative").asInt()
            );

        } catch (Exception e) {
            throw new IllegalStateException("OpenAI 결과 파싱 실패: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String userText) {
        return """
너는 독서 대화에서 사용자의 사고 스타일을 0~100 점수로 평가하는 분석가다.
다음은 사용자가 AI와 나눈 '사용자 메시지'만 모은 텍스트이다.

[평가축 정의]
- critic: 비판/평가/문제제기/논리적 반박이 강할수록 높음
- emotion: 감정 표현/정서 단어/느낌 묘사가 강할수록 높음
- analysis: 구조화/원인-결과/근거/요약/추론이 강할수록 높음
- empathy: 등장인물/타인 관점 이해/공감/배려가 강할수록 높음
- creative: 상상/확장 해석/비유/새 관점/가정이 강할수록 높음

[입력 텍스트]
""" + userText + """

JSON 스키마에 맞춰 점수만 출력해.
""";
    }

    public record Scores(int critic, int emotion, int analysis, int empathy, int creative) {}
}
