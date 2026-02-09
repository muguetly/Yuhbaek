package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.dto.analytics.ThinkingStyleStatsResponse;
import com.example.Yuhbaek.repository.analytics.ThinkingStyleScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class ThinkingStyleStatsService {

    private final ThinkingStyleScoreRepository repository;

    public ThinkingStyleStatsResponse getStats(Long userId, Integer year, Integer month) {
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

        Object[] row = repository.avgScores(userId, start, end);

        // row 자체가 null은 거의 없지만, 값은 null일 수 있음
        int critic = toInt(row, 0);
        int emotion = toInt(row, 1);
        int analysis = toInt(row, 2);
        int empathy = toInt(row, 3);
        int creative = toInt(row, 4);

        return new ThinkingStyleStatsResponse(critic, emotion, analysis, empathy, creative);
    }

    private int toInt(Object[] row, int idx) {
        if (row == null || row.length <= idx || row[idx] == null) return 0;
        // AVG 결과는 Double로 오는 경우 많음 -> 반올림
        return (int) Math.round(((Number) row[idx]).doubleValue());
    }
}
