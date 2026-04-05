package com.project.fitness.service.weeklyprogress;

import com.project.fitness.model.CardioSession;
import com.project.fitness.model.FoodEntry;
import com.project.fitness.model.WeightLog;
import com.project.fitness.model.WorkoutSet;

import java.util.List;

/**
 * Immutable container for the raw data fetched for a 7-day window.
 * Passed between the fetcher, aggregator, and summary builder.
 */
public record WeekData(
    List<FoodEntry>     food,
    List<WorkoutSet>    sets,
    List<CardioSession> cardio,
    List<WeightLog>     weights
) {}
