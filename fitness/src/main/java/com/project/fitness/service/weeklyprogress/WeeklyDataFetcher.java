package com.project.fitness.service.weeklyprogress;

import com.project.fitness.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Issues the four bulk repository queries needed for the weekly report.
 * Keeps all I/O in one place so the aggregators stay pure and testable.
 */
@Component
@RequiredArgsConstructor
public class WeeklyDataFetcher {

    private final FoodEntryRepository     foodEntryRepository;
    private final WorkoutSetRepository    workoutSetRepository;
    private final CardioSessionRepository cardioSessionRepository;
    private final WeightLogRepository     weightLogRepository;

    /** Fetches 7 days of data ending on {@code today} (inclusive). */
    public WeekData fetch(String userId, LocalDate today) {
        LocalDateTime start = today.minusDays(6).atStartOfDay();
        LocalDateTime end   = today.plusDays(1).atStartOfDay();

        return new WeekData(
            foodEntryRepository.findByUserIdAndDateRange(userId, start, end),
            workoutSetRepository.findByUserIdAndDateRange(userId, start, end),
            cardioSessionRepository.findByUserIdAndDateRange(userId, start, end),
            weightLogRepository.findByUserIdAndDateRange(userId, start, end)
        );
    }
}
