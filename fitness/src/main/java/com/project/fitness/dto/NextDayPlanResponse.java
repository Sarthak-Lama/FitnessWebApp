package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class NextDayPlanResponse {

    private LocalDate planDate;            // tomorrow

    // ── Calorie target for tomorrow ───────────────────────
    private double targetCalories;         // daily goal ± adjustment
    private double baseGoal;              // goal without adjustment
    private double adjustment;            // kcal to add/remove vs base

    // ── Meal plan ─────────────────────────────────────────
    private List<MealSuggestionItem> mealPlan;
    private double mealPlanTotalCalories;
    private double mealPlanTotalProtein;
    private double mealPlanTotalCarbs;
    private double mealPlanTotalFat;

    // ── Exercise plan ─────────────────────────────────────
    private ExercisePlan exercisePlan;

    // ── Rest day logic ────────────────────────────────────
    private boolean restDayRecommended;
    private int consecutiveGymDays;
    private String restDayReason;

    // ── Reminders ─────────────────────────────────────────
    private List<String> reminders;

    // ── Summary ───────────────────────────────────────────
    private String overallMessage;
    private String fitnessGoal;
    private String activityLevel;
}
