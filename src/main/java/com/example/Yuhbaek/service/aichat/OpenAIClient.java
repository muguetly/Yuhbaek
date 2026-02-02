package com.example.Yuhbaek.service.aichat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenAIClient {

    private final WebClient openaiWebClient;

    public OpenAIClient(@Qualifier("openaiWebClient") WebClient openaiWebClient) {
        this.openaiWebClient = openaiWebClient;
    }

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-5}")
    private String model;

    // ✅ 타임아웃/재시도 설정 (필요하면 properties로 빼도 됨)
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(75); // 60~90 추천
    private static final String FALLBACK_TEXT =
            "앗 지금 답장이 조금 늦는다… 😭 방금 말한 거 이어서 한 번만 더 말해줄래?";

    public String generateWithSystem(String systemPrompt, List<Map<String, String>> chat) {

        List<Map<String, Object>> input = new ArrayList<>();
        input.add(Map.of("role", "system", "content", systemPrompt));
        for (var m : chat) {
            input.add(Map.of(
                    "role", m.get("role"),
                    "content", m.get("content")
            ));
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "input", input
        );

        return openaiWebClient.post()
                .uri("/v1/responses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        r -> r.bodyToMono(String.class)
                                .defaultIfEmpty("(empty error body)")
                                .flatMap(msg -> Mono.error(new RuntimeException("OpenAI API error: " + msg)))
                )
                .bodyToMono(Map.class)
                .timeout(REQUEST_TIMEOUT) // ✅ 30초 -> 75초
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(300)) // ✅ 2번 재시도
                                .maxBackoff(Duration.ofSeconds(2))
                                // 4xx(잘못된 요청) 같은 건 재시도해도 의미 없어서 제외하고 싶으면 여기서 분기 가능
                                .filter(ex -> !(ex instanceof IllegalArgumentException))
                )
                .map(this::extractTextFromResponsesApi)
                .onErrorReturn(FALLBACK_TEXT) // ✅ 타임아웃/네트워크 에러 등은 자연스러운 폴백
                .block();
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponsesApi(Map resp) {
        try {
            List<Map<String, Object>> output = (List<Map<String, Object>>) resp.get("output");
            if (output == null) return resp.toString();

            for (Map<String, Object> item : output) {
                if (!"message".equals(item.get("type"))) continue;

                List<Map<String, Object>> content = (List<Map<String, Object>>) item.get("content");
                if (content == null) continue;

                for (Map<String, Object> c : content) {
                    if ("output_text".equals(c.get("type"))) {
                        Object text = c.get("text");
                        return text != null ? text.toString() : resp.toString();
                    }
                }
            }
            return resp.toString();
        } catch (Exception e) {
            return resp.toString();
        }
    }
}