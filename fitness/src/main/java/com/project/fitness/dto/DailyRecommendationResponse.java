package com.project.fitness.dto;

import lombok.Data;

import java.util.List;

@Data
public class DailyRecommendationResponse {

    // ── Rest-day state ────────────────────────────────
    private boolean restDayRecommended;
    private String  restDayReason;
    private int     consecutiveGymDays;

    // ── Tomorrow's exercise plan ──────────────────────
    private String exercisePlan;        // e.g. "Walk 30 min" / "Strength Training"
    private String exercisePlanDetail;  // longer instruction

    // ── Tomorrow's meal suggestions ───────────────────
    private List<MealSuggestion> mealSuggestions;

    // ── Today's summary (used on the frontend) ────────
    private int    gymSetsToday;
    private int    cardioSessionsToday;
    private double totalCaloriesToday;
}
