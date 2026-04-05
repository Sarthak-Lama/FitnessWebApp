package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExercisePlan {
    private String sessionType;         // GYM, CARDIO, LIGHT_ACTIVITY, REST
    private String sessionTitle;
    private String targetMuscleGroup;   // for GYM sessions
    private List<String> exercises;
    private int durationMinutes;
    private String intensity;           // LOW, MODERATE, HIGH
    private double estimatedCaloriesBurn;
    private String notes;
}
