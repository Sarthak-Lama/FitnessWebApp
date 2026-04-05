package com.project.fitness.service;

import com.project.fitness.dto.DailyRecommendationResponse;
import com.project.fitness.dto.MealSuggestion;
import com.project.fitness.model.*;
import com.project.fitness.repository.CardioSessionRepository;
import com.project.fitness.repository.UserProfileRepository;
import com.project.fitness.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates tomorrow's meal + exercise recommendations based solely on
 * today's gym (WorkoutSet) and cardio (CardioSession) logs.
 *
 * No dependency on the generic Activity table.
 */
@Service
@RequiredArgsConstructor
public class DailyRecommendationService {

    private final WorkoutSetRepository    workoutSetRepository;
    private final CardioSessionRepository cardioSessionRepository;
    private final UserProfileRepository   userProfileRepository;

    public DailyRecommendationResponse generate(String userId) {

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = todayStart.plusDays(1);

        List<WorkoutSet>    todaySets   = workoutSetRepository.findByUserIdAndDateRange(userId, todayStart, todayEnd);
        List<CardioSession> todayCardio = cardioSessionRepository.findByUserIdAndDateRange(userId, todayStart, todayEnd);
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);

        int     consecutiveGymDays = countConsecutiveGymDays(userId);
        boolean isRestDay          = consecutiveGymDays >= 3;
        boolean hadGymToday        = !todaySets.isEmpty();
        boolean hadCardioToday     = !todayCardio.isEmpty();

        double calsBurned = todaySets.stream()
                .mapToDouble(s -> s.getCaloriesBurned() != null ? s.getCaloriesBurned() : 0.0).sum()
                + todayCardio.stream()
                .mapToDouble(c -> c.getCaloriesBurned() != null ? c.getCaloriesBurned() : 0.0).sum();

        FitnessGoal goal = profileOpt.map(UserProfile::getFitnessGoal).orElse(FitnessGoal.MAINTENANCE);

        DailyRecommendationResponse resp = new DailyRecommendationResponse();
        resp.setRestDayRecommended(isRestDay);
        resp.setConsecutiveGymDays(consecutiveGymDays);
        resp.setGymSetsToday(todaySets.size());
        resp.setCardioSessionsToday(todayCardio.size());
        resp.setTotalCaloriesToday(Math.round(calsBurned * 10.0) / 10.0);

        // ── Exercise plan for tomorrow ────────────────────
        if (isRestDay) {
            resp.setRestDayReason("You have logged gym sessions for " + consecutiveGymDays +
                    " consecutive days. Rest allows muscles to repair and grow stronger.");
            resp.setExercisePlan("Rest Day");
            resp.setExercisePlanDetail("Take a full rest day. Light walking (15–20 min) is fine " +
                    "but avoid any resistance training.");
        } else if (hadGymToday && hadCardioToday) {
            resp.setExercisePlan("Active Recovery — Walk 20 min");
            resp.setExercisePlanDetail("You trained hard today. Tomorrow, a gentle 20-minute walk " +
                    "will promote blood flow without taxing your muscles.");
        } else if (hadGymToday) {
            resp.setExercisePlan("Cardio — Brisk Walk or Cycling 30 min");
            resp.setExercisePlanDetail("Follow today's strength session with 30 minutes of moderate " +
                    "cardio tomorrow. Treadmill, outdoor walk, or cycling all work well.");
        } else if (hadCardioToday) {
            resp.setExercisePlan("Strength Training");
            resp.setExercisePlanDetail("Great cardio day! Tomorrow is ideal for a gym session. " +
                    "Focus on compound lifts that match your " + formatGoal(goal) + " goal.");
        } else {
            resp.setExercisePlan("Brisk Walk — 30 to 45 min");
            resp.setExercisePlanDetail("No activity logged today. Start gently tomorrow with a " +
                    "30–45 minute brisk walk to build momentum.");
        }

        // ── Meal suggestions for tomorrow ─────────────────
        resp.setMealSuggestions(buildMeals(goal, isRestDay, hadGymToday));
        return resp;
    }

    // ── Consecutive gym-day counter ───────────────────────
    private int countConsecutiveGymDays(String userId) {
        List<WorkoutSet> all = workoutSetRepository.findByUserIdOrderByLoggedAtDesc(userId);
        Set<LocalDate> gymDates = all.stream()
                .map(w -> w.getLoggedAt().toLocalDate())
                .collect(Collectors.toSet());

        int count = 0;
        LocalDate d = LocalDate.now();
        while (gymDates.contains(d)) {
            count++;
            d = d.minusDays(1);
        }
        return count;
    }

    // ── Meal plan builder ─────────────────────────────────
    private List<MealSuggestion> buildMeals(FitnessGoal goal, boolean isRestDay, boolean hadGym) {
        List<MealSuggestion> meals = new ArrayList<>();

        switch (goal) {

            case WEIGHT_LOSS -> {
                meals.add(ms("BREAKFAST", "Dhido + Gundruk Soup",
                        320, 11, 56, 5,
                        "High-fibre start that keeps you full. Use mustard oil sparingly."));
                meals.add(ms("LUNCH", "Dal Bhat (half rice, double dal)",
                        430, 18, 65, 6,
                        "Reduce rice to one katori; fill the plate with dal and saag instead."));
                meals.add(ms("SNACK", "Roasted Bhatmas (50 g)",
                        115, 10, 7, 7,
                        "High-protein, low-calorie snack that prevents overeating at dinner."));
                meals.add(ms("DINNER",
                        isRestDay ? "Steamed Veg Momo (6 pcs) + Gundruk Soup"
                                  : "Steamed Chicken Momo (8 pcs) + Gundruk Soup",
                        isRestDay ? 240 : 290,
                        isRestDay ? 8  : 14,
                        isRestDay ? 33 : 34,
                        isRestDay ? 5  : 6,
                        "Light dinner. Avoid fried momo or sel roti in the evening."
                ));
            }

            case MUSCLE_GAIN, WEIGHT_GAIN -> {
                meals.add(ms("BREAKFAST", "3 Boiled Eggs + Chiura (80 g) + Curd",
                        550, 32, 56, 19,
                        "Complete amino acids from eggs paired with quick-digesting chiura."));
                meals.add(ms("LUNCH", "Dal Bhat + Chicken Curry (200 g)",
                        780, 48, 86, 19,
                        "Largest meal of the day. Add a spoon of ghee for healthy calorie density."));
                meals.add(ms("SNACK",
                        hadGym ? "Sukuti (60 g) + Chiura (50 g)" : "Sweet Lassi (300 ml)",
                        hadGym ? 340 : 190,
                        hadGym ? 32  : 9,
                        hadGym ? 22  : 28,
                        hadGym ? 11  : 5,
                        hadGym ? "High-protein Nepali post-gym snack — portable and filling."
                               : "Dairy protein + carbs for recovery on a rest or cardio day."));
                meals.add(ms("DINNER", "Kheer (200 g) + Roti (2 pcs)",
                        580, 18, 88, 14,
                        "Casein from milk digests slowly overnight, supporting muscle repair."));
            }

            case ENDURANCE -> {
                meals.add(ms("BREAKFAST", "Chiura (100 g) + Curd + Banana",
                        460, 12, 87, 5,
                        "Carb-forward breakfast to top up glycogen before endurance work."));
                meals.add(ms("LUNCH", "Dal Bhat + Saag (150 g)",
                        520, 16, 92, 6,
                        "Steady complex carbs from rice; iron from saag supports oxygen delivery."));
                meals.add(ms("SNACK", "Nimbu Pani + Roasted Peanuts (30 g)",
                        200, 8, 18, 12,
                        "Natural electrolytes from lemon water; peanuts add lasting energy."));
                meals.add(ms("DINNER", "Thukpa (Noodle Soup)",
                        390, 14, 60, 8,
                        "Warming, easy-to-digest carb + protein meal after a long session."));
            }

            default -> {  // MAINTENANCE, FLEXIBILITY, etc.
                meals.add(ms("BREAKFAST", "Roti (2 pcs) + Curd + Banana",
                        420, 13, 72, 8,
                        "Balanced macros to energise the morning without a calorie spike."));
                meals.add(ms("LUNCH", "Dal Bhat + Aloo Tama or Cauliflower Curry",
                        540, 17, 88, 9,
                        "Classic Nepali plate — ideal macro balance for maintenance days."));
                meals.add(ms("SNACK", "Chiya (200 ml) + Bhatmas (30 g)",
                        140, 10, 10, 6,
                        "Light energising snack; the soy protein helps bridge lunch and dinner."));
                meals.add(ms("DINNER", "Steamed Chicken Momo (8 pcs)",
                        380, 24, 38, 10,
                        "Protein-rich dinner that is not too heavy before sleep."));
            }
        }

        return meals;
    }

    // ── Helpers ───────────────────────────────────────────
    private MealSuggestion ms(String type, String name,
                               int cal, double prot, double carbs, double fat, String tip) {
        return MealSuggestion.builder()
                .mealType(type).foodName(name)
                .calories(cal).proteinG(prot).carbsG(carbs).fatG(fat)
                .tip(tip).build();
    }

    private String formatGoal(FitnessGoal goal) {
        return goal.name().replace("_", " ").toLowerCase();
    }
}
