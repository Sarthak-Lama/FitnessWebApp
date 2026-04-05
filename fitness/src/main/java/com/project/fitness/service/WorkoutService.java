package com.project.fitness.service;

import com.project.fitness.dto.CardioSessionRequest;
import com.project.fitness.dto.CardioSessionResponse;
import com.project.fitness.dto.ExerciseProgressResponse;
import com.project.fitness.dto.WorkoutSetRequest;
import com.project.fitness.dto.WorkoutSetResponse;
import com.project.fitness.model.*;
import com.project.fitness.repository.CardioSessionRepository;
import com.project.fitness.repository.UserProfileRepository;
import com.project.fitness.repository.UserRepository;
import com.project.fitness.repository.WorkoutSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutSetRepository workoutSetRepository;
    private final CardioSessionRepository cardioSessionRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // ===================== GYM SETS =====================

    public WorkoutSetResponse logSet(WorkoutSetRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        // PR check: get previous best BEFORE saving
        Optional<Double> prevBest = workoutSetRepository
                .findMaxWeightByUserIdAndExerciseName(request.getUserId(), request.getExerciseName());

        double newWeight = request.getWeightKg() != null ? request.getWeightKg() : 0.0;
        boolean isPR = prevBest.isEmpty() || (newWeight > 0 && newWeight > prevBest.get());

        boolean prFlag = isPR && newWeight > 0;
        WorkoutSet set = WorkoutSet.builder()
                .user(user)
                .exerciseName(request.getExerciseName().trim())
                .muscleGroup(request.getMuscleGroup())
                .sets(request.getSets())
                .reps(request.getReps())
                .weightKg(newWeight)
                .caloriesBurned(request.getCaloriesBurned())
                .isPR(prFlag)
                .loggedAt(request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now())
                .build();

        WorkoutSet saved = workoutSetRepository.save(set);
        WorkoutSetResponse response = mapSetToResponse(saved);
        response.setPR(prFlag);
        response.setPreviousBestKg(prevBest.orElse(null));
        return response;
    }

    public List<WorkoutSetResponse> getTodaySets(String userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return workoutSetRepository.findByUserIdAndDateRange(userId, start, end)
                .stream().map(w -> { WorkoutSetResponse r = mapSetToResponse(w); r.setPR(Boolean.TRUE.equals(w.getIsPR())); return r; })
                .collect(Collectors.toList());
    }

    public List<WorkoutSetResponse> getAllSets(String userId) {
        return workoutSetRepository.findByUserIdOrderByLoggedAtDesc(userId)
                .stream().map(w -> { WorkoutSetResponse r = mapSetToResponse(w); r.setPR(Boolean.TRUE.equals(w.getIsPR())); return r; })
                .collect(Collectors.toList());
    }

    public List<WorkoutSetResponse> getSetsByMuscleGroup(String userId, MuscleGroup muscleGroup) {
        return workoutSetRepository.findByUserIdAndMuscleGroup(userId, muscleGroup)
                .stream().map(w -> { WorkoutSetResponse r = mapSetToResponse(w); r.setPR(Boolean.TRUE.equals(w.getIsPR())); return r; })
                .collect(Collectors.toList());
    }

    public List<ExerciseProgressResponse> getExerciseProgress(String userId) {
        List<String> exercises = workoutSetRepository.findDistinctExerciseNamesByUserId(userId);
        return exercises.stream()
                .map(ex -> buildExerciseProgress(userId, ex))
                .sorted(Comparator.comparing(ExerciseProgressResponse::getLastLoggedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private ExerciseProgressResponse buildExerciseProgress(String userId, String exerciseName) {
        List<WorkoutSet> history = workoutSetRepository.findByUserIdAndExerciseNameAsc(userId, exerciseName);

        ExerciseProgressResponse resp = new ExerciseProgressResponse();
        resp.setExerciseName(exerciseName);
        resp.setTotalSessions(history.size());

        if (history.isEmpty()) return resp;

        WorkoutSet latest = history.get(history.size() - 1);
        resp.setMuscleGroup(latest.getMuscleGroup());
        resp.setLastLoggedAt(latest.getLoggedAt());

        // Current session max weight (same day as latest entry)
        LocalDateTime dayStart = latest.getLoggedAt().toLocalDate().atStartOfDay();
        double currentBest = history.stream()
                .filter(w -> !w.getLoggedAt().isBefore(dayStart))
                .mapToDouble(w -> w.getWeightKg() != null ? w.getWeightKg() : 0.0)
                .max().orElse(0.0);
        if (currentBest > 0) resp.setCurrentBestKg(currentBest);

        // Previous sessions max weight (before the current day)
        OptionalDouble prevOpt = history.stream()
                .filter(w -> w.getLoggedAt().isBefore(dayStart))
                .mapToDouble(w -> w.getWeightKg() != null ? w.getWeightKg() : 0.0)
                .max();

        if (prevOpt.isEmpty()) {
            resp.setTrend("NEW");
        } else {
            double prevBest = prevOpt.getAsDouble();
            resp.setPreviousBestKg(prevBest > 0 ? prevBest : null);
            if (currentBest > prevBest) {
                resp.setTrend("UP");
                resp.setImprovementKg(Math.round((currentBest - prevBest) * 10.0) / 10.0);
                resp.setImprovementPct(prevBest > 0
                        ? Math.round((currentBest - prevBest) / prevBest * 1000.0) / 10.0 : null);
            } else if (currentBest < prevBest && currentBest > 0) {
                resp.setTrend("DOWN");
                resp.setImprovementKg(Math.round((currentBest - prevBest) * 10.0) / 10.0);
            } else {
                resp.setTrend("SAME");
            }
        }

        // Last 5 sets
        List<WorkoutSet> recent = history.subList(Math.max(0, history.size() - 5), history.size());
        resp.setRecentSets(recent.stream()
                .map(w -> { WorkoutSetResponse r = mapSetToResponse(w); r.setPR(Boolean.TRUE.equals(w.getIsPR())); return r; })
                .collect(Collectors.toList()));

        return resp;
    }

    private WorkoutSetResponse mapSetToResponse(WorkoutSet w) {
        WorkoutSetResponse r = new WorkoutSetResponse();
        r.setId(w.getId());
        r.setUserId(w.getUser().getId());
        r.setExerciseName(w.getExerciseName());
        r.setMuscleGroup(w.getMuscleGroup());
        r.setSets(w.getSets());
        r.setReps(w.getReps());
        r.setWeightKg(w.getWeightKg());
        r.setCaloriesBurned(w.getCaloriesBurned());
        r.setLoggedAt(w.getLoggedAt());
        r.setCreatedAt(w.getCreatedAt());
        return r;
    }

    // ===================== CARDIO =====================

    public CardioSessionResponse logCardio(CardioSessionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        double calories = request.getCaloriesBurned() != null
                ? request.getCaloriesBurned()
                : estimateCalories(request, userProfileRepository.findByUserId(request.getUserId()));

        CardioSession session = CardioSession.builder()
                .user(user)
                .activityType(request.getActivityType())
                .cardioType(request.getCardioType())
                .durationMinutes(request.getDurationMinutes())
                .distanceKm(request.getDistanceKm())
                .caloriesBurned(Math.round(calories * 10.0) / 10.0)
                .loggedAt(request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now())
                .build();

        return mapCardioToResponse(cardioSessionRepository.save(session));
    }

    public List<CardioSessionResponse> getTodayCardio(String userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return cardioSessionRepository.findByUserIdAndDateRange(userId, start, end)
                .stream().map(this::mapCardioToResponse).collect(Collectors.toList());
    }

    public List<CardioSessionResponse> getAllCardio(String userId) {
        return cardioSessionRepository.findByUserIdOrderByLoggedAtDesc(userId)
                .stream().map(this::mapCardioToResponse).collect(Collectors.toList());
    }

    /**
     * MET-based calorie estimation:  kcal = MET × weight(kg) × duration(hours)
     */
    private double estimateCalories(CardioSessionRequest request, java.util.Optional<UserProfile> profileOpt) {
        double weightKg = profileOpt.map(UserProfile::getWeightKg).orElse(70.0);
        double hours = request.getDurationMinutes() / 60.0;
        double met = getMet(request.getActivityType(), request.getCardioType());
        return met * weightKg * hours;
    }

    private double getMet(ActivityType type, CardioType cardioType) {
        boolean treadmill = cardioType == CardioType.TREADMILL;
        return switch (type) {
            case WALKING   -> treadmill ? 3.8 : 3.5;
            case RUNNING   -> treadmill ? 9.0 : 8.5;
            case CYCLING   -> treadmill ? 5.5 : 6.0;
            case HITT      -> 10.0;
            case JUMP_ROPE -> 11.0;
            case CARDIO    -> treadmill ? 5.0 : 4.5;
            default        -> 4.0;
        };
    }

    private CardioSessionResponse mapCardioToResponse(CardioSession c) {
        CardioSessionResponse r = new CardioSessionResponse();
        r.setId(c.getId());
        r.setUserId(c.getUser().getId());
        r.setActivityType(c.getActivityType());
        r.setCardioType(c.getCardioType());
        r.setDurationMinutes(c.getDurationMinutes());
        r.setCaloriesBurned(c.getCaloriesBurned());
        r.setDistanceKm(c.getDistanceKm());
        r.setLoggedAt(c.getLoggedAt());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}
