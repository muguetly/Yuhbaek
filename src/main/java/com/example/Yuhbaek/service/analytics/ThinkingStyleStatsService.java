package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.dto.analytics.ThinkingStyleStatsResponse;
import com.example.Yuhbaek.repository.analytics.ThinkingStyleAvgRow;
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

        ThinkingStyleAvgRow row = repository.avgScores(userId, start, end);

        int critic   = toInt(row == null ? null : row.getCritic());
        int emotion  = toInt(row == null ? null : row.getEmotion());
        int analysis = toInt(row == null ? null : row.getAnalysis());
        int empathy  = toInt(row == null ? null : row.getEmpathy());
        int creative = toInt(row == null ? null : row.getCreative());

        return new ThinkingStyleStatsResponse(critic, emotion, analysis, empathy, creative);
    }

    private int toInt(Double v) {
        if (v == null) return 0;
        return (int) Math.round(v);
    }
}
