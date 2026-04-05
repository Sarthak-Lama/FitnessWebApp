package com.project.fitness.dto;

import com.project.fitness.model.MuscleGroup;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExerciseProgressResponse {
    private String exerciseName;
    private MuscleGroup muscleGroup;
    private Double currentBestKg;
    private Double previousBestKg;
    private String trend;           // "NEW", "UP", "DOWN", "SAME"
    private Double improvementKg;
    private Double improvementPct;
    private LocalDateTime lastLoggedAt;
    private int totalSessions;
    private List<WorkoutSetResponse> recentSets; // last 5
}
