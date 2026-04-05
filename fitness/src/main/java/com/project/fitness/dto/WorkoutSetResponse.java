package com.project.fitness.dto;

import com.project.fitness.model.MuscleGroup;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkoutSetResponse {
    private String id;
    private String userId;
    private String exerciseName;
    private MuscleGroup muscleGroup;
    private Integer sets;
    private Integer reps;
    private Double weightKg;
    private Double caloriesBurned;
    private boolean isPR;          // Personal Record flag
    private Double previousBestKg; // previous best before this set
    private LocalDateTime loggedAt;
    private LocalDateTime createdAt;
}
