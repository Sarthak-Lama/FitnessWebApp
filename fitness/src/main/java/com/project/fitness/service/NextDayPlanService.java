package com.project.fitness.service;

import com.project.fitness.dto.*;
import com.project.fitness.model.UserProfile;
import com.project.fitness.model.WorkoutSet;
import com.project.fitness.repository.UserProfileRepository;
import com.project.fitness.repository.WorkoutSetRepository;
import com.project.fitness.service.nextdayplan.ExercisePlanBuilder;
import com.project.fitness.service.nextdayplan.MealPlanBuilder;
import com.project.fitness.service.nextdayplan.ReminderBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestrates tomorrow's personalised plan by delegating to focused sub-components.
 *
 * <ul>
 *   <li>{@link MealPlanBuilder}    — five-slot Nepali meal plan</li>
 *   <li>{@link ExercisePlanBuilder} — gym / cardio / rest-day session</li>
 *   <li>{@link ReminderBuilder}    — reminders and motivational message</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class NextDayPlanService {

    private final UserProfileRepository userProfileRepository;
    private final WorkoutSetRepository  workoutSetRepository;
    private final CalorieSummaryService calorieSummaryService;
    private final MealPlanBuilder       mealPlanBuilder;
    private final ExercisePlanBuilder   exercisePlanBuilder;
    private final ReminderBuilder       reminderBuilder;

    public NextDayPlanResponse generatePlan(String userId) {
        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        UserProfile profile      = userProfileRepository.findByUserId(userId).orElse(null);
        DailyCalorieSummaryResponse summary = calorieSummaryService.getSummary(userId, today);

        String goalStr   = profile != null && profile.getFitnessGoal() != null
                ? profile.getFitnessGoal().name() : "MAINTENANCE";
        double baseGoal  = summary.getDailyGoal();
        double adjustment = summary.getNextDayAdjustment();
        double target    = Math.max(1200, Math.min(baseGoal + adjustment, 4500));

        int  consecGym = countConsecutiveGymDays(userId, today);
        boolean restDay = consecGym >= 3;

        List<MealSuggestionItem> meals    = mealPlanBuilder.build(goalStr, target, summary);
        ExercisePlan             exercise = exercisePlanBuilder.build(profile, goalStr, restDay, consecGym, userId, today);
        List<String>             reminders = reminderBuilder.buildReminders(profile, summary, consecGym, restDay);
        String                   overall  = reminderBuilder.buildOverallMessage(goalStr, summary, restDay, consecGym, target);

        double totCal  = round(meals.stream().mapToDouble(MealSuggestionItem::getCalories).sum());
        double totProt = round(meals.stream().mapToDouble(MealSuggestionItem::getProteinGrams).sum());
        double totCarb = round(meals.stream().mapToDouble(MealSuggestionItem::getCarbsGrams).sum());
        double totFat  = round(meals.stream().mapToDouble(MealSuggestionItem::getFatGrams).sum());

        return NextDayPlanResponse.builder()
                .planDate(tomorrow)
                .targetCalories(round(target))
                .baseGoal(round(baseGoal))
                .adjustment(round(adjustment))
                .mealPlan(meals)
                .mealPlanTotalCalories(totCal)
                .mealPlanTotalProtein(totProt)
                .mealPlanTotalCarbs(totCarb)
                .mealPlanTotalFat(totFat)
                .exercisePlan(exercise)
                .restDayRecommended(restDay)
                .consecutiveGymDays(consecGym)
                .restDayReason(restDay
                    ? "You have logged " + consecGym + " consecutive gym days. Rest allows muscles to " +
                      "repair and grow stronger. Overtraining leads to fatigue and injury risk."
                    : null)
                .reminders(reminders)
                .overallMessage(overall)
                .fitnessGoal(goalStr)
                .activityLevel(profile != null && profile.getActivityLevel() != null
                    ? profile.getActivityLevel().name() : "SEDENTARY")
                .build();
    }

    /** Counts how many consecutive days (including today) the user has logged gym sets. */
    private int countConsecutiveGymDays(String userId, LocalDate today) {
        int count = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate day = today.minusDays(i);
            LocalDateTime s = day.atStartOfDay();
            List<WorkoutSet> sets = workoutSetRepository.findByUserIdAndDateRange(userId, s, s.plusDays(1));
            if (!sets.isEmpty()) count++;
            else break;
        }
        return count;
    }

    private double round(double v) { return Math.round(v * 10.0) / 10.0; }
}
