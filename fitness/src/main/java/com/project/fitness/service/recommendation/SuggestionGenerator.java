package com.project.fitness.service.recommendation;

import com.project.fitness.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates Nepali food suggestions and lifestyle advice sourced from the
 * user's gym (WorkoutSet) and cardio (CardioSession) logs plus fitness goal.
 * No ActivityType dependency.
 */
@Component
public class SuggestionGenerator {

    public List<String> generate(List<WorkoutSet> gymSets,
                                 List<CardioSession> cardioSessions,
                                 Optional<UserProfile> profileOpt) {
        List<String> out = new ArrayList<>();

        int totalCardioMin = cardioSessions.stream()
                .mapToInt(c -> c.getDurationMinutes() != null ? c.getDurationMinutes() : 0).sum();
        int totalGymSets   = gymSets.stream()
                .mapToInt(s -> s.getSets() != null ? s.getSets() : 1).sum();

        FitnessGoal goal = profileOpt.map(UserProfile::getFitnessGoal)
                                      .orElse(FitnessGoal.MAINTENANCE);

        out.add("Pre-workout (1–2 hrs before): Eat light and energising food.");
        out.add("Post-workout (within 30–60 min): Prioritise protein and carbs for recovery.");

        addGoalFoodSuggestions(out, goal);

        // Hydration tip driven by cardio volume, not activity type
        if (!cardioSessions.isEmpty() && totalCardioMin >= 30) {
            out.add("After your cardio session, drink nimbu pani (lemon water) or ORS to replenish electrolytes.");
        }

        // Long-session tip driven by combined volume
        if (totalCardioMin > 60 || totalGymSets > 15) {
            out.add("Long training day — consider a mid-workout snack like a banana or a handful of roasted peanuts.");
        }

        out.add("Sleep 7–9 hours nightly for optimal recovery and muscle repair.");
        out.add("Stay consistent — aim for at least 4 active days per week.");
        return out;
    }

    private void addGoalFoodSuggestions(List<String> out, FitnessGoal goal) {
        switch (goal) {
            case WEIGHT_LOSS -> {
                out.add("Nepali foods for weight loss: Dhido, Gundruk soup, steamed Momo, dal with less rice, cucumber and tomato achar.");
                out.add("Avoid: Sel Roti, Jeri, fried Momo, and sugary Chiya.");
                out.add("Have Dal Bhat with more dal and less rice to maintain satiety with fewer calories.");
            }
            case MUSCLE_GAIN, WEIGHT_GAIN -> {
                out.add("Nepali foods for muscle gain: Dal Bhat (extra dal), Sukuti (dried meat), eggs, Kheer, curd/lassi.");
                out.add("Post-gym Nepali protein options: Sukuti with chiura, 2–3 boiled eggs, or a bowl of dal with rice.");
                out.add("Add ghee to your Dal Bhat for healthy calorie density.");
            }
            case ENDURANCE -> {
                out.add("Nepali carb-loading foods: Chiura (beaten rice), Sel Roti, Dal Bhat — great endurance fuel.");
                out.add("Pre-long-session: A small bowl of chiura with curd provides sustained energy.");
            }
            default -> {
                out.add("Balanced Nepali diet: Dal Bhat with seasonal saag provides excellent macro balance.");
                out.add("Healthy snacks: Roasted bhatmas (soybeans), chiura with curd, or a banana.");
            }
        }
    }
}
