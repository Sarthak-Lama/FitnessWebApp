package com.project.fitness.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DailyRecommendationResponse {

    // ── Rest-day state ────────────────────────────────
    private boolean restDayRecommended;
    private String  restDayReason;
    private int     consecutiveGymDays;

    // ── Tomorrow's exercise plan (simple) ────────────
    private String exercisePlan;        // e.g. "Walk 30 min" / "Strength Training"
    private String exercisePlanDetail;  // longer instruction

    // ── Tomorrow's meal suggestions (simple 4-slot) ──
    private List<MealSuggestion> mealSuggestions;

    // ── Today's workout summary ───────────────────────
    private int    gymSetsToday;
    private int    cardioSessionsToday;
    private double totalCaloriesToday;   // calories burned from exercise

    // ── Today's Analysis (food + activity) ───────────
    private List<String> analysisPoints;
    private double caloriesConsumed;
    private double proteinConsumedG;
    private int    calorieGoal;
    private int    proteinGoalG;
    private int    totalCardioMinutes;

    // ── Training Insights (from generators) ──────────
    private List<String> improvements;   // training quality improvement tips
    private List<String> suggestions;    // Nepali food & lifestyle advice
    private List<String> safetyTips;     // safety & medical-aware guidance

    // ── Detailed next-day plan (from NextDayPlanService) ──
    private LocalDate planDate;
    private double    targetCalories;       // tomorrow's calorie target
    private double    baseGoal;             // daily goal without adjustment
    private double    calorieAdjustment;    // kcal added/removed vs base goal

    private List<MealSuggestionItem> detailedMealPlan;   // 5-slot Nepali meal plan
    private double mealPlanTotalCalories;
    private double mealPlanTotalProtein;
    private double mealPlanTotalCarbs;
    private double mealPlanTotalFat;

    private ExercisePlan detailedExercisePlan;  // with muscle group, exercises, sets/reps
    private List<String> reminders;             // personalised reminders
    private String       overallMessage;        // motivational summary message
    private String       fitnessGoal;
    private String       activityLevel;
}
