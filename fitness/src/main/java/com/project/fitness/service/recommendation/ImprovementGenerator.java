package com.project.fitness.service.recommendation;

import com.project.fitness.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates training-quality improvement tips sourced from the user's
 * gym (WorkoutSet) and cardio (CardioSession) logs.
 * No ActivityType dependency.
 */
@Component
public class ImprovementGenerator {

    public List<String> generate(List<WorkoutSet> gymSets,
                                 List<CardioSession> cardioSessions,
                                 Optional<UserProfile> profileOpt) {
        List<String> out = new ArrayList<>();

        boolean hadGym    = !gymSets.isEmpty();
        boolean hadCardio = !cardioSessions.isEmpty();
        FitnessGoal goal  = profileOpt.map(UserProfile::getFitnessGoal)
                                       .orElse(FitnessGoal.MAINTENANCE);

        if (hadGym)    addGymImprovements(out, gymSets);
        if (hadCardio) addCardioImprovements(out, cardioSessions);
        if (!hadGym && !hadCardio) addIdleTips(out);

        addGoalTips(out, goal, gymSets);
        return out;
    }

    private void addGymImprovements(List<String> out, List<WorkoutSet> sets) {
        int totalSets = sets.stream()
                .mapToInt(s -> s.getSets() != null ? s.getSets() : 1).sum();
        boolean hasPR = sets.stream()
                .anyMatch(s -> Boolean.TRUE.equals(s.getIsPR()));

        out.add("Focus on progressive overload — increase weight or reps by a small increment each session.");
        out.add("Ensure proper breathing: exhale on exertion, inhale on the return phase.");

        if (hasPR) {
            out.add("Personal record achieved today — allow 48–72 hrs before training the same muscle group heavily again.");
        }
        if (totalSets < 9) {
            out.add("Today's volume is on the lower side. Aim for 3–4 sets per exercise for an effective muscle stimulus.");
        } else if (totalSets > 20) {
            out.add("High training volume today — prioritise 7–9 hours of sleep to allow full recovery.");
        }
    }

    private void addCardioImprovements(List<String> out, List<CardioSession> sessions) {
        int totalMin = sessions.stream()
                .mapToInt(c -> c.getDurationMinutes() != null ? c.getDurationMinutes() : 0).sum();

        if (totalMin < 20) {
            out.add("Short cardio session — try building up to 30 minutes for full cardiovascular benefit.");
        } else if (totalMin > 60) {
            out.add("Great endurance session. Stay well-hydrated and consider an electrolyte drink afterwards.");
        }
        out.add("Vary cardio intensity across sessions — alternate steady-state and interval work for better adaptation.");
    }

    private void addIdleTips(List<String> out) {
        out.add("No gym or cardio logged today. Logging consistently is the first step to improvement.");
        out.add("Aim for at least 3–4 active sessions per week to see measurable results.");
    }

    private void addGoalTips(List<String> out, FitnessGoal goal, List<WorkoutSet> gymSets) {
        switch (goal) {
            case WEIGHT_LOSS ->
                out.add("For weight loss, maintain a moderate calorie deficit and pair gym sessions with cardio.");
            case MUSCLE_GAIN, WEIGHT_GAIN -> {
                boolean hasCompound = gymSets.stream().anyMatch(s -> {
                    String n = s.getExerciseName() != null ? s.getExerciseName().toLowerCase() : "";
                    return n.contains("squat") || n.contains("deadlift") || n.contains("bench")
                            || n.contains("row")   || n.contains("press");
                });
                if (!hasCompound && !gymSets.isEmpty()) {
                    out.add("Include compound lifts (squat, deadlift, bench press, rows) to maximise muscle stimulus.");
                }
                out.add("Track weekly sets per muscle group — aim for 10–20 sets per group for hypertrophy.");
            }
            case ENDURANCE ->
                out.add("Increase weekly cardio volume by no more than 10% per week to reduce injury risk.");
            default ->
                out.add("Reassess your training plan every 4–6 weeks to keep progressing.");
        }
    }
}
