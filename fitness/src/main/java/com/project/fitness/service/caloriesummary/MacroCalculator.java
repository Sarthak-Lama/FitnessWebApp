package com.project.fitness.service.caloriesummary;

import com.project.fitness.model.UserProfile;
import org.springframework.stereotype.Component;

/**
 * Daily calorie goal and macro-target calculations.
 */
@Component
public class MacroCalculator {

    /**
     * Returns the daily calorie target for the user.
     * Respects an explicit {@code dailyCalorieTarget} on the profile; otherwise
     * adjusts TDEE by fitness goal.
     */
    public double calculateDailyGoal(UserProfile p, double tdee) {
        if (p == null) return tdee;
        if (p.getDailyCalorieTarget() != null) return p.getDailyCalorieTarget();
        if (p.getFitnessGoal() == null) return tdee;
        return switch (p.getFitnessGoal()) {
            case WEIGHT_LOSS              -> round(tdee - 500);
            case WEIGHT_GAIN, MUSCLE_GAIN -> round(tdee + 300);
            case ENDURANCE                -> round(tdee + 100);
            default                       -> tdee;
        };
    }

    /**
     * Returns macro targets as {@code [proteinGrams, carbsGrams, fatGrams]}
     * using goal-specific macro splits.
     *
     * <ul>
     *   <li>Weight Loss  → 40 % P / 30 % C / 30 % F</li>
     *   <li>Muscle Gain  → 35 % P / 45 % C / 20 % F</li>
     *   <li>Endurance    → 20 % P / 60 % C / 20 % F</li>
     *   <li>Default      → 30 % P / 45 % C / 25 % F</li>
     * </ul>
     */
    public double[] calculateMacroTargets(double goalKcal, UserProfile p) {
        double pPct = 0.30, cPct = 0.45;
        if (p != null && p.getFitnessGoal() != null) {
            pPct = switch (p.getFitnessGoal()) {
                case WEIGHT_LOSS -> 0.40;
                case MUSCLE_GAIN -> 0.35;
                case ENDURANCE   -> 0.20;
                default          -> 0.30;
            };
            cPct = switch (p.getFitnessGoal()) {
                case WEIGHT_LOSS -> 0.30;
                case MUSCLE_GAIN -> 0.45;
                case ENDURANCE   -> 0.60;
                default          -> 0.45;
            };
        }
        double fPct = 1 - pPct - cPct;
        return new double[]{
            round(goalKcal * pPct / 4),   // protein g  (4 kcal/g)
            round(goalKcal * cPct / 4),   // carbs g
            round(goalKcal * fPct / 9),   // fat g      (9 kcal/g)
        };
    }

    private double round(double v) { return Math.round(v * 10.0) / 10.0; }
}
