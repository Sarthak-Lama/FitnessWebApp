package com.project.fitness.service.nextdayplan;

import com.project.fitness.dto.DailyCalorieSummaryResponse;
import com.project.fitness.dto.MealSuggestionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Assembles the five-slot meal plan (breakfast, 2 snacks, lunch, dinner)
 * for a given fitness goal and calorie target.
 */
@Component
@RequiredArgsConstructor
public class MealPlanBuilder {

    private final NepaliMealDatabase mealDatabase;

    public List<MealSuggestionItem> build(String goal, double targetCal,
                                          DailyCalorieSummaryResponse summary) {
        double bfPct  = 0.25;
        double lnPct  = 0.35;
        double dnPct  = 0.25;
        double snPct  = 0.075;   // per snack slot

        switch (goal) {
            case "MUSCLE_GAIN", "WEIGHT_GAIN" -> dnPct = 0.28;
            case "WEIGHT_LOSS"                -> { bfPct = 0.30; dnPct = 0.20; }
            case "ENDURANCE"                  -> { lnPct = 0.40; dnPct = 0.22; snPct = 0.065; }
            default -> { /* keep defaults */ }
        }

        return List.of(
            mealDatabase.selectBest("BREAKFAST",       goal, targetCal * bfPct),
            mealDatabase.selectBest("MORNING_SNACK",   goal, targetCal * snPct),
            mealDatabase.selectBest("LUNCH",           goal, targetCal * lnPct),
            mealDatabase.selectBest("AFTERNOON_SNACK", goal, targetCal * snPct),
            mealDatabase.selectBest("DINNER",          goal, targetCal * dnPct)
        );
    }
}
