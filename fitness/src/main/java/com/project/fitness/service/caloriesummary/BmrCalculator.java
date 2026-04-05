package com.project.fitness.service.caloriesummary;

import com.project.fitness.model.ActivityLevel;
import com.project.fitness.model.Gender;
import com.project.fitness.model.UserProfile;
import org.springframework.stereotype.Component;

/**
 * Mifflin-St Jeor BMR and TDEE calculations.
 */
@Component
public class BmrCalculator {

    private static final double MULT_SEDENTARY      = 1.20;
    private static final double MULT_LIGHTLY_ACTIVE = 1.375;
    private static final double MULT_GYM_GOING      = 1.55;
    private static final double MULT_VERY_ACTIVE    = 1.725;

    /** Returns BMR in kcal using Mifflin-St Jeor. Falls back to 1800 if profile is incomplete. */
    public double calculateBmr(UserProfile p) {
        if (p == null || p.getWeightKg() == null || p.getHeightCm() == null || p.getAge() == null) {
            return 1800;
        }
        boolean female = p.getGender() != null && p.getGender() == Gender.FEMALE;
        double base = 10 * p.getWeightKg() + 6.25 * p.getHeightCm() - 5 * p.getAge();
        return base + (female ? -161 : 5);
    }

    /** Returns the TDEE activity multiplier for the given profile. */
    public double getActivityMultiplier(UserProfile p) {
        if (p == null || p.getActivityLevel() == null) return MULT_SEDENTARY;
        return switch (p.getActivityLevel()) {
            case LIGHTLY_ACTIVE -> MULT_LIGHTLY_ACTIVE;
            case GYM_GOING      -> MULT_GYM_GOING;
            case VERY_ACTIVE    -> MULT_VERY_ACTIVE;
            default             -> MULT_SEDENTARY;
        };
    }
}
