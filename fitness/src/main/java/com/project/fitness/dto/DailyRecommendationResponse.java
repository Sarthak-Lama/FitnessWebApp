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

    // ── Today's workout summary ───────────────────────
    private int    gymSetsToday;
    private int    cardioSessionsToday;
    private double totalCaloriesToday;   // calories burned from exercise

    // ── Today's Analysis (food + activity) ───────────
    private List<String> analysisPoints;   // bullet-point insights
    private double caloriesConsumed;       // kcal eaten today
    private double proteinConsumedG;       // protein eaten today (g)
    private int    calorieGoal;            // from user profile (0 = unknown)
    private int    proteinGoalG;           // from user profile (0 = unknown)
    private int    totalCardioMinutes;     // sum of cardio session durations today
}
