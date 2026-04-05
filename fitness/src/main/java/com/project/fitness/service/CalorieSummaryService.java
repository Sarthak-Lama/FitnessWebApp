package com.project.fitness.service;

import com.project.fitness.dto.DailyCalorieSummaryResponse;
import com.project.fitness.model.*;
import com.project.fitness.repository.*;
import com.project.fitness.service.caloriesummary.BmrCalculator;
import com.project.fitness.service.caloriesummary.MacroCalculator;
import com.project.fitness.service.caloriesummary.NextDayTextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestrates the daily calorie summary by delegating each concern
 * to a focused sub-component.
 *
 * <ul>
 *   <li>{@link BmrCalculator}    — Mifflin-St Jeor BMR &amp; TDEE</li>
 *   <li>{@link MacroCalculator}  — daily goal and macro-target splits</li>
 *   <li>{@link NextDayTextBuilder} — next-day recommendation text</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CalorieSummaryService {

    // ── Repositories ──────────────────────────────────────
    private final FoodEntryRepository      foodEntryRepository;
    private final WorkoutSetRepository     workoutSetRepository;
    private final CardioSessionRepository  cardioSessionRepository;
    private final UserProfileRepository    userProfileRepository;

    // ── Sub-components ────────────────────────────────────
    private final BmrCalculator      bmrCalculator;
    private final MacroCalculator    macroCalculator;
    private final NextDayTextBuilder nextDayTextBuilder;

    public DailyCalorieSummaryResponse getSummary(String userId, LocalDate date) {
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);

        // ── Date windows ──
        LocalDateTime start  = date.atStartOfDay();
        LocalDateTime end    = start.plusDays(1);
        LocalDateTime yStart = start.minusDays(1);

        // ── Today: food ──
        List<FoodEntry> todayFood = foodEntryRepository.findByUserIdAndDateRange(userId, start, end);
        double calIn   = round(todayFood.stream().mapToDouble(f -> orZero(f.getCalories())).sum());
        double protein = round(todayFood.stream().mapToDouble(f -> orZero(f.getProtein())).sum());
        double carbs   = round(todayFood.stream().mapToDouble(f -> orZero(f.getCarbs())).sum());
        double fat     = round(todayFood.stream().mapToDouble(f -> orZero(f.getFat())).sum());

        // ── Today: exercise burn ──
        List<WorkoutSet>    sets   = workoutSetRepository.findByUserIdAndDateRange(userId, start, end);
        List<CardioSession> cardio = cardioSessionRepository.findByUserIdAndDateRange(userId, start, end);
        double exerciseBurn = round(
            sets.stream().mapToDouble(w -> orZero(w.getCaloriesBurned())).sum() +
            cardio.stream().mapToDouble(c -> orZero(c.getCaloriesBurned())).sum()
        );

        // ── BMR / TDEE ──
        double bmr  = bmrCalculator.calculateBmr(profile);
        double tdee = round(bmr * bmrCalculator.getActivityMultiplier(profile));
        double totalOut = round(exerciseBurn + bmr);

        // ── Goal and macros ──
        double dailyGoal    = macroCalculator.calculateDailyGoal(profile, tdee);
        double[] macroTgts  = macroCalculator.calculateMacroTargets(dailyGoal, profile);

        // ── Balance ──
        double defSurp = round(calIn - dailyGoal);
        String status  = Math.abs(defSurp) <= 150 ? "ON_TRACK" : defSurp > 0 ? "SURPLUS" : "DEFICIT";
        double balPct  = dailyGoal > 0 ? round((calIn / dailyGoal) * 100) : 0;

        // ── Yesterday carry-over ──
        List<FoodEntry> yFood = foodEntryRepository.findByUserIdAndDateRange(userId, yStart, start);
        Double yDefSurp   = null;
        String yStatus    = null;
        double yCarryOver = 0;
        if (!yFood.isEmpty()) {
            double yCalIn = yFood.stream().mapToDouble(f -> orZero(f.getCalories())).sum();
            yDefSurp  = round(yCalIn - dailyGoal);
            yStatus   = Math.abs(yDefSurp) <= 150 ? "ON_TRACK" : yDefSurp > 0 ? "SURPLUS" : "DEFICIT";
            yCarryOver = round(-yDefSurp / 3.0);
        }

        // ── Next-day recommendation ──
        String nextDayRec = nextDayTextBuilder.build(defSurp, yDefSurp, profile, calIn, dailyGoal);
        double nextDayAdj = round(-defSurp * 0.5);

        return DailyCalorieSummaryResponse.builder()
                .date(date)
                .caloriesIn(calIn)
                .proteinGrams(protein).carbsGrams(carbs).fatGrams(fat)
                .caloriesBurnedExercise(exerciseBurn)
                .bmr(round(bmr)).tdee(tdee).totalCaloriesOut(totalOut)
                .dailyGoal(dailyGoal)
                .fitnessGoal(profile != null && profile.getFitnessGoal() != null
                    ? profile.getFitnessGoal().name() : "MAINTENANCE")
                .activityLevel(profile != null && profile.getActivityLevel() != null
                    ? profile.getActivityLevel().name() : "SEDENTARY")
                .proteinTargetGrams(macroTgts[0]).carbsTargetGrams(macroTgts[1]).fatTargetGrams(macroTgts[2])
                .deficitSurplus(defSurp).balanceStatus(status).balancePct(balPct)
                .yesterdayDeficitSurplus(yDefSurp).yesterdayStatus(yStatus)
                .yesterdayCarryOverAdjustment(yCarryOver)
                .nextDayRecommendation(nextDayRec).nextDayAdjustment(nextDayAdj)
                .weightKg(profile != null ? profile.getWeightKg() : null)
                .heightCm(profile != null ? profile.getHeightCm() : null)
                .age(profile != null ? profile.getAge() : null)
                .gender(profile != null && profile.getGender() != null
                    ? profile.getGender().name() : null)
                .build();
    }

    private double orZero(Double v) { return v != null ? v : 0.0; }
    private double round(double v)  { return Math.round(v * 10.0) / 10.0; }
}
