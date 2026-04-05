package com.project.fitness.service.nextdayplan;

import com.project.fitness.dto.ExercisePlan;
import com.project.fitness.model.MuscleGroup;
import com.project.fitness.model.UserProfile;
import com.project.fitness.model.WorkoutSet;
import com.project.fitness.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Determines tomorrow's exercise session based on fitness goal, consecutive gym
 * days, and the most recently trained muscle group.
 */
@Component
@RequiredArgsConstructor
public class ExercisePlanBuilder {

    private final WorkoutSetRepository workoutSetRepository;

    private static final Map<MuscleGroup, List<String>> GYM_EXERCISES = new LinkedHashMap<>();
    private static final List<MuscleGroup> GYM_ROTATION =
        List.of(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS,
                MuscleGroup.SHOULDERS, MuscleGroup.ARMS, MuscleGroup.CORE);

    static {
        GYM_EXERCISES.put(MuscleGroup.CHEST,     List.of("Bench Press 4×8", "Incline Dumbbell Press 3×10", "Cable Flyes 3×12", "Push-ups 3×15", "Dips 3×10"));
        GYM_EXERCISES.put(MuscleGroup.BACK,      List.of("Deadlift 4×5", "Pull-ups 4×8", "Bent-over Rows 3×10", "Lat Pulldown 3×12", "Seated Cable Row 3×12"));
        GYM_EXERCISES.put(MuscleGroup.LEGS,      List.of("Barbell Squat 4×8", "Romanian Deadlift 3×10", "Leg Press 3×12", "Leg Curls 3×12", "Calf Raises 4×15"));
        GYM_EXERCISES.put(MuscleGroup.SHOULDERS, List.of("Overhead Press 4×8", "Lateral Raises 3×15", "Front Raises 3×12", "Face Pulls 3×15", "Arnold Press 3×10"));
        GYM_EXERCISES.put(MuscleGroup.ARMS,      List.of("Barbell Curl 4×10", "Hammer Curl 3×12", "Tricep Pushdown 4×12", "Skull Crushers 3×10", "Preacher Curl 3×12"));
        GYM_EXERCISES.put(MuscleGroup.CORE,      List.of("Plank 3×60s", "Hanging Leg Raises 3×15", "Russian Twists 3×20", "Cable Crunches 3×15", "Mountain Climbers 3×30s"));
    }

    public ExercisePlan build(UserProfile profile, String goal, boolean restDay,
                               int consecGym, String userId, LocalDate today) {
        if (restDay) return activeRecoveryPlan();

        double weightKg = (profile != null && profile.getWeightKg() != null) ? profile.getWeightKg() : 70.0;
        boolean isCardioGoal  = "ENDURANCE".equals(goal);
        boolean isWeightLoss  = "WEIGHT_LOSS".equals(goal);
        boolean isStrength    = "MUSCLE_GAIN".equals(goal) || "WEIGHT_GAIN".equals(goal);

        if (isCardioGoal || (isWeightLoss && consecGym % 2 == 0)) {
            String title = isCardioGoal ? "Endurance Cardio Session" : "Fat-Burn Cardio Session";
            return buildCardioSession(goal, weightKg, title);
        }

        MuscleGroup lastGroup = resolveLastGroup(userId, today);
        MuscleGroup nextGroup = nextInRotation(lastGroup, isWeightLoss);

        List<String> exercises = GYM_EXERCISES.getOrDefault(nextGroup,
            List.of("Compound lift 4×8", "Accessory work 3×12", "Core finisher 3×15"));

        String groupLabel = formatGroupName(nextGroup);
        return ExercisePlan.builder()
                .sessionType("GYM")
                .sessionTitle(groupLabel + " Day")
                .targetMuscleGroup(nextGroup.name())
                .exercises(exercises)
                .durationMinutes(60)
                .intensity(isStrength ? "HIGH" : "MODERATE")
                .estimatedCaloriesBurn(isStrength ? 250 : 200)
                .notes(buildGymNotes(nextGroup, isStrength))
                .build();
    }

    // ── Private builders ──────────────────────────────────
    private ExercisePlan activeRecoveryPlan() {
        return ExercisePlan.builder()
                .sessionType("REST")
                .sessionTitle("Active Recovery Day")
                .exercises(List.of(
                    "Light stretching — 15 min",
                    "Yoga / foam rolling — 20 min",
                    "Casual walk — 20 min at easy pace"))
                .durationMinutes(55)
                .intensity("LOW")
                .estimatedCaloriesBurn(120)
                .notes("Your muscles need rest to grow. Keep movement light — no heavy lifting. " +
                       "Focus on sleep quality (7–9 hours) and stay well hydrated.")
                .build();
    }

    private ExercisePlan buildCardioSession(String goal, double weightKg, String title) {
        boolean endurance = "ENDURANCE".equals(goal);
        List<String> exercises = endurance
            ? List.of("Warm-up jog 5 min", "Steady-state run 30 min (6–7 km/h)", "Incline walk 10 min", "Cool-down walk 5 min")
            : List.of("Warm-up walk 5 min", "HIIT: 30s sprint / 90s walk × 8 rounds", "Steady walk 10 min", "Cool-down stretch 5 min");
        int dur   = endurance ? 50 : 40;
        double met = endurance ? 8.0 : 9.5;
        double burn = Math.round(met * weightKg * (dur / 60.0) * 10.0) / 10.0;
        return ExercisePlan.builder()
                .sessionType("CARDIO").sessionTitle(title)
                .exercises(exercises)
                .durationMinutes(dur)
                .intensity(endurance ? "MODERATE" : "HIGH")
                .estimatedCaloriesBurn(burn)
                .notes(endurance
                    ? "Keep heart rate at 65–75% of max (roughly 130–150 bpm). Hydrate every 15 min."
                    : "HIIT maximises fat burn in less time. Rest 90s fully between sprints.")
                .build();
    }

    MuscleGroup resolveLastGroup(String userId, LocalDate today) {
        for (int i = 0; i < 5; i++) {
            LocalDate day = today.minusDays(i);
            LocalDateTime s = day.atStartOfDay();
            List<WorkoutSet> sets = workoutSetRepository.findByUserIdAndDateRange(userId, s, s.plusDays(1));
            if (!sets.isEmpty()) {
                return sets.stream()
                        .filter(w -> w.getMuscleGroup() != null)
                        .collect(Collectors.groupingBy(WorkoutSet::getMuscleGroup, Collectors.counting()))
                        .entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(MuscleGroup.CHEST);
            }
        }
        return MuscleGroup.CHEST;
    }

    private MuscleGroup nextInRotation(MuscleGroup last, boolean weightLoss) {
        List<MuscleGroup> rotation = weightLoss
            ? List.of(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.SHOULDERS, MuscleGroup.CORE)
            : GYM_ROTATION;
        int idx = rotation.indexOf(last);
        return rotation.get(idx < 0 ? 0 : (idx + 1) % rotation.size());
    }

    private String buildGymNotes(MuscleGroup group, boolean isStrength) {
        String base = switch (group) {
            case CHEST     -> "Focus on full range of motion — squeeze at the top of each press. ";
            case BACK      -> "Keep a neutral spine on all rows and deadlifts. Drive elbows back. ";
            case LEGS      -> "Never skip leg day! Squats and deadlifts are the foundation of all strength. ";
            case SHOULDERS -> "Avoid shrugging on overhead work — keep shoulder blades packed down. ";
            case ARMS      -> "Superset biceps and triceps for maximum pump and time efficiency. ";
            case CORE      -> "Brace your core like you're about to get punched on every rep. ";
            default        -> "Stay focused on form over weight. ";
        };
        return base + (isStrength
            ? "Eat your protein snack (bhatmas or sukuti) within 30 min post-workout."
            : "Have a light protein snack post-session — boiled egg or curd works well.");
    }

    private String formatGroupName(MuscleGroup g) {
        String s = g.name();
        return s.charAt(0) + s.substring(1).toLowerCase().replace('_', ' ');
    }
}
