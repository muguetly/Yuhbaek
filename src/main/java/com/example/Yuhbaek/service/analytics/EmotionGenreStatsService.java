package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.dto.analytics.DonutSliceDto;
import com.example.Yuhbaek.dto.analytics.EmotionGenreStatsResponse;
import com.example.Yuhbaek.entity.analytics.EmotionType;
import com.example.Yuhbaek.repository.analytics.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmotionGenreStatsService {

    public enum Mode {
        EMOTION_TO_GENRE,
        GENRE_TO_EMOTION
    }

    private final EmotionLogRepository emotionLogRepository;

    public EmotionGenreStatsResponse getStats(
            Long userId,
            Mode mode,
            Integer emotionId,
            String genre,
            Integer year,
            Integer month
    ) {
        LocalDateTime start;
        LocalDateTime end;

        if (year == null) {
            start = LocalDateTime.of(2000, 1, 1, 0, 0);
            end = LocalDateTime.now();
        } else if (month == null) {
            start = LocalDate.of(year, 1, 1).atStartOfDay();
            end = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        } else {
            YearMonth ym = YearMonth.of(year, month);
            start = ym.atDay(1).atStartOfDay();
            end = ym.atEndOfMonth().atTime(23, 59, 59);
        }

        List<DonutSliceDto> slices = new ArrayList<>();

        if (mode == Mode.EMOTION_TO_GENRE) {
            if (emotionId == null) {
                throw new IllegalArgumentException("emotionId는 필수입니다.");
            }

            // emotionId가 실제 유효한 값인지 먼저 검증
            EmotionType.fromId(emotionId);

            List<Object[]> rows = emotionLogRepository.countGenresByEmotion(userId, emotionId, start, end);

            for (Object[] row : rows) {
                String label = (String) row[0];   // 장르명
                long value = ((Number) row[1]).longValue();
                slices.add(new DonutSliceDto(label, value));
            }

            return new EmotionGenreStatsResponse(mode.name(), emotionId, null, slices);

        } else {
            if (genre == null || genre.isBlank()) {
                throw new IllegalArgumentException("genre는 필수입니다.");
            }

            String normalized = genre.trim();
            List<Object[]> rows = emotionLogRepository.countEmotionsByGenre(userId, normalized, start, end);

            for (Object[] row : rows) {
                int rawEmotionId = ((Number) row[0]).intValue();
                long value = ((Number) row[1]).longValue();

                // 숫자 id를 사람이 읽을 수 있는 감정 라벨로 변환
                String label = EmotionType.fromId(rawEmotionId).getLabel();

                slices.add(new DonutSliceDto(label, value));
            }

            return new EmotionGenreStatsResponse(mode.name(), null, normalized, slices);
        }
    }
}