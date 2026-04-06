package com.project.fitness.service;

import com.project.fitness.dto.DailyRecommendationResponse;
import com.project.fitness.dto.MealSuggestion;
import com.project.fitness.model.*;
import com.project.fitness.repository.CardioSessionRepository;
import com.project.fitness.repository.FoodEntryRepository;
import com.project.fitness.repository.UserProfileRepository;
import com.project.fitness.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRecommendationService {

    private final WorkoutSetRepository    workoutSetRepository;
    private final CardioSessionRepository cardioSessionRepository;
    private final UserProfileRepository   userProfileRepository;
    private final FoodEntryRepository     foodEntryRepository;

    public DailyRecommendationResponse generate(String userId) {
        log.info("[DailyRec] Generating for userId={}", userId);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = todayStart.plusDays(1);

        List<WorkoutSet>    todaySets   = workoutSetRepository.findByUserIdAndDateRange(userId, todayStart, todayEnd);
        List<CardioSession> todayCardio = cardioSessionRepository.findByUserIdAndDateRange(userId, todayStart, todayEnd);
        List<FoodEntry>     todayFood   = foodEntryRepository.findByUserIdAndDateRange(userId, todayStart, todayEnd);
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);

        log.info("[DailyRec] userId={} | gymSets={} | cardioSessions={} | foodEntries={} | profilePresent={}",
                userId, todaySets.size(), todayCardio.size(), todayFood.size(), profileOpt.isPresent());

        int     consecutiveGymDays = countConsecutiveGymDays(userId);
        boolean isRestDay          = consecutiveGymDays >= 3;
        boolean hadGymToday        = !todaySets.isEmpty();
        boolean hadCardioToday     = !todayCardio.isEmpty();

        double calsBurned = todaySets.stream()
                .mapToDouble(s -> s.getCaloriesBurned() != null ? s.getCaloriesBurned() : 0.0).sum()
                + todayCardio.stream()
                .mapToDouble(c -> c.getCaloriesBurned() != null ? c.getCaloriesBurned() : 0.0).sum();

        double calsConsumed = todayFood.stream()
                .mapToDouble(f -> f.getCalories() != null ? f.getCalories() : 0.0).sum();
        double proteinConsumed = todayFood.stream()
                .mapToDouble(f -> f.getProtein() != null ? f.getProtein() : 0.0).sum();
        int cardioMinutes = todayCardio.stream()
                .mapToInt(c -> c.getDurationMinutes() != null ? c.getDurationMinutes() : 0).sum();

        FitnessGoal goal        = profileOpt.map(UserProfile::getFitnessGoal).orElse(FitnessGoal.MAINTENANCE);
        int         calorieGoal = profileOpt.map(UserProfile::getDailyCalorieTarget).filter(v -> v != null && v > 0).orElse(0);
        int         proteinGoal = profileOpt.map(UserProfile::getDailyProteinTarget).filter(v -> v != null && v > 0).orElse(0);

        DailyRecommendationResponse resp = new DailyRecommendationResponse();
        resp.setRestDayRecommended(isRestDay);
        resp.setConsecutiveGymDays(consecutiveGymDays);
        resp.setGymSetsToday(todaySets.size());
        resp.setCardioSessionsToday(todayCardio.size());
        resp.setTotalCaloriesToday(Math.round(calsBurned * 10.0) / 10.0);
        resp.setCaloriesConsumed(Math.round(calsConsumed * 10.0) / 10.0);
        resp.setProteinConsumedG(Math.round(proteinConsumed * 10.0) / 10.0);
        resp.setCalorieGoal(calorieGoal);
        resp.setProteinGoalG(proteinGoal);
        resp.setTotalCardioMinutes(cardioMinutes);

        // ── Build Today's Analysis points ─────────────────
        resp.setAnalysisPoints(buildAnalysisPoints(
                todayFood, todaySets, todayCardio,
                calsConsumed, proteinConsumed, calsBurned,
                calorieGoal, proteinGoal, cardioMinutes));

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
        log.info("[DailyRec] userId={} | restDay={} | consecutiveDays={} | plan=\"{}\"",
                userId, isRestDay, consecutiveGymDays, resp.getExercisePlan());
        return resp;
    }

    // ── Today's Analysis builder ──────────────────────────
    private List<String> buildAnalysisPoints(
            List<FoodEntry> food, List<WorkoutSet> sets, List<CardioSession> cardio,
            double calsConsumed, double proteinConsumed, double calsBurned,
            int calorieGoal, int proteinGoal, int cardioMinutes) {

        List<String> points = new ArrayList<>();

        // Nutrition
        if (food.isEmpty()) {
            points.add("No food logged today — make sure to track your meals for accurate analysis.");
        } else {
            if (calorieGoal > 0) {
                int pct = (int) Math.round(calsConsumed * 100.0 / calorieGoal);
                String status = pct < 85 ? "under target" : pct > 115 ? "over target" : "on track";
                points.add(String.format("Calories: %.0f kcal consumed of your %d kcal goal (%d%% — %s).",
                        calsConsumed, calorieGoal, pct, status));
            } else {
                points.add(String.format("Calories: %.0f kcal consumed today across %d food entries.",
                        calsConsumed, food.size()));
            }

            if (proteinGoal > 0) {
                String pStatus = proteinConsumed < proteinGoal * 0.8 ? "below target — try to add a protein-rich meal"
                        : proteinConsumed > proteinGoal * 1.2 ? "above target"
                        : "on track";
                points.add(String.format("Protein: %.0fg consumed of your %dg target (%s).",
                        proteinConsumed, proteinGoal, pStatus));
            } else {
                points.add(String.format("Protein: %.0fg consumed today.", proteinConsumed));
            }
        }

        // Gym
        if (sets.isEmpty()) {
            points.add("No gym session logged today.");
        } else {
            int totalSets = sets.stream().mapToInt(s -> s.getSets() != null ? s.getSets() : 1).sum();
            long exercises = sets.stream().map(WorkoutSet::getExerciseName).distinct().count();
            points.add(String.format("Gym: %d sets across %d exercise(s) logged — %.0f kcal burned.",
                    totalSets, exercises, calsBurned > 0 ? calsBurned : 0.0));
        }

        // Cardio
        if (cardio.isEmpty()) {
            points.add("No cardio logged today.");
        } else {
            double cardioCals = cardio.stream()
                    .mapToDouble(c -> c.getCaloriesBurned() != null ? c.getCaloriesBurned() : 0.0).sum();
            points.add(String.format("Cardio: %d session(s), %d minutes total — %.0f kcal burned.",
                    cardio.size(), cardioMinutes, cardioCals));
        }

        // Net balance if enough data
        if (!food.isEmpty() && calorieGoal > 0 && calsBurned > 0) {
            double net = calsConsumed - calsBurned;
            points.add(String.format("Net intake after exercise: %.0f kcal (consumed %.0f − burned %.0f).",
                    net, calsConsumed, calsBurned));
        }

        return points;
    }

    // ── Consecutive gym-day counter ───────────────────────
    private int countConsecutiveGymDays(String userId) {
        List<WorkoutSet> all = workoutSetRepository.findByUserIdOrderByLoggedAtDesc(userId);
        Set<LocalDate> gymDates = all.stream()
                .filter(w -> w.getLoggedAt() != null)          // guard: loggedAt is nullable in schema
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
