package com.project.fitness.dto;

import com.project.fitness.model.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkoutSetRequest {
    @NotNull
    private String userId;

    @NotBlank
    private String exerciseName;

    @NotNull
    private MuscleGroup muscleGroup;

    @NotNull
    private Integer sets;

    @NotNull
    private Integer reps;

    private Double weightKg;       // null / 0 = bodyweight

    private Double caloriesBurned;
    private LocalDateTime loggedAt;
}
