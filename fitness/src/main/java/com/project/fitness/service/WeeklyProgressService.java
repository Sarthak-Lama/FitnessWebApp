package com.project.fitness.service;

import com.project.fitness.dto.WeeklyProgressDay;
import com.project.fitness.dto.WeeklyProgressResponse;
import com.project.fitness.service.weeklyprogress.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Orchestrates the weekly progress report by delegating each concern to a
 * focused sub-component.
 *
 * <ul>
 *   <li>{@link WeeklyDataFetcher}    — 4 bulk DB queries</li>
 *   <li>{@link DailyAggregator}      — per-day calorie/macro/weight rollup</li>
 *   <li>{@link WeeklySummaryBuilder} — week-level averages, totals, weight change</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class WeeklyProgressService {

    private final WeeklyDataFetcher    dataFetcher;
    private final DailyAggregator      dailyAggregator;
    private final WeeklySummaryBuilder summaryBuilder;

    public WeeklyProgressResponse getWeekly(String userId) {
        LocalDate today = LocalDate.now();
        WeekData data   = dataFetcher.fetch(userId, today);
        List<WeeklyProgressDay> days = dailyAggregator.buildDays(today, data);
        return summaryBuilder.build(days, data);
    }
}
