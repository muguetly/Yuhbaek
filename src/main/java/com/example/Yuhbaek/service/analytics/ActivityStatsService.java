package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.dto.analytics.HourlyActivityDto;
import com.example.Yuhbaek.repository.aichat.AIChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ActivityStatsService {

    private final AIChatMessageRepository messageRepository;

    public List<HourlyActivityDto> getHourlyStats(Long userId, Integer year, Integer month) {

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

        List<Object[]> rows = messageRepository.aggregateUserActivityByHour(userId, start, end);

        Map<Integer, HourlyActivityDto> map = new HashMap<>();
        for (Object[] row : rows) {
            int hour = ((Number) row[0]).intValue();
            long msgCount = ((Number) row[1]).longValue();
            long charCount = row[2] == null ? 0L : ((Number) row[2]).longValue(); // null 방지
            map.put(hour, new HourlyActivityDto(hour, msgCount, charCount));
        }

        List<HourlyActivityDto> result = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            result.add(map.getOrDefault(h, new HourlyActivityDto(h, 0, 0)));
        }
        return result;
    }
}
