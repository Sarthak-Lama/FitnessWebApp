package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyCalorieSummaryResponse {

    private LocalDate date;

    // ── Calories IN ──────────────────────────────────────
    private double caloriesIn;
    private double proteinGrams;
    private double carbsGrams;
    private double fatGrams;

    // ── Calories OUT ─────────────────────────────────────
    private double caloriesBurnedExercise;   // gym + cardio today
    private double bmr;                      // Basal Metabolic Rate
    private double tdee;                     // BMR × activity multiplier
    private double totalCaloriesOut;         // exercise + BMR (full day burn)

    // ── Goal ─────────────────────────────────────────────
    private double dailyGoal;               // target intake based on fitness goal
    private String fitnessGoal;
    private String activityLevel;

    // ── Macro targets ────────────────────────────────────
    private double proteinTargetGrams;
    private double carbsTargetGrams;
    private double fatTargetGrams;

    // ── Balance ──────────────────────────────────────────
    /** Positive = surplus (ate more than goal), Negative = deficit (ate less) */
    private double deficitSurplus;
    /** "DEFICIT" | "SURPLUS" | "ON_TRACK" */
    private String balanceStatus;
    /** How far off from goal as percentage */
    private double balancePct;

    // ── Yesterday carry-over ─────────────────────────────
    private Double yesterdayDeficitSurplus;
    private String yesterdayStatus;
    private double yesterdayCarryOverAdjustment;  // recommended kcal to add/reduce today

    // ── Next-day recommendation ──────────────────────────
    private String nextDayRecommendation;
    private double nextDayAdjustment;   // kcal to add (positive) or reduce (negative) tomorrow

    // ── Profile snapshot ─────────────────────────────────
    private Double weightKg;
    private Double heightCm;
    private Integer age;
    private String gender;
}
