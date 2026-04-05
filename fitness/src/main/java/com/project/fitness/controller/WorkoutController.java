package com.project.fitness.controller;

import com.project.fitness.dto.*;
import com.project.fitness.model.MuscleGroup;
import com.project.fitness.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    // ---- Gym Sets ----
    @PostMapping("/set")
    public ResponseEntity<WorkoutSetResponse> logSet(@Valid @RequestBody WorkoutSetRequest request) {
        return ResponseEntity.ok(workoutService.logSet(request));
    }

    @GetMapping("/sets/today")
    public ResponseEntity<List<WorkoutSetResponse>> getTodaySets(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(workoutService.getTodaySets(userId));
    }

    @GetMapping("/sets")
    public ResponseEntity<List<WorkoutSetResponse>> getAllSets(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(workoutService.getAllSets(userId));
    }

    @GetMapping("/sets/muscle/{muscleGroup}")
    public ResponseEntity<List<WorkoutSetResponse>> getSetsByMuscleGroup(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable MuscleGroup muscleGroup) {
        return ResponseEntity.ok(workoutService.getSetsByMuscleGroup(userId, muscleGroup));
    }

    @GetMapping("/exercises/progress")
    public ResponseEntity<List<ExerciseProgressResponse>> getExerciseProgress(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(workoutService.getExerciseProgress(userId));
    }

    // ---- Cardio ----
    @PostMapping("/cardio")
    public ResponseEntity<CardioSessionResponse> logCardio(@Valid @RequestBody CardioSessionRequest request) {
        return ResponseEntity.ok(workoutService.logCardio(request));
    }

    @GetMapping("/cardio/today")
    public ResponseEntity<List<CardioSessionResponse>> getTodayCardio(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(workoutService.getTodayCardio(userId));
    }

    @GetMapping("/cardio")
    public ResponseEntity<List<CardioSessionResponse>> getAllCardio(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(workoutService.getAllCardio(userId));
    }
}
