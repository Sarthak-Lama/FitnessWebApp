package com.project.fitness.dto;

import com.project.fitness.model.ActivityLevel;
import com.project.fitness.model.FitnessGoal;
import com.project.fitness.model.Gender;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileResponse {
    private String id;
    private String userId;
    private Double weightKg;
    private Double heightCm;
    private Double targetWeightKg;
    private Integer age;
    private Gender gender;
    private FitnessGoal fitnessGoal;
    private ActivityLevel activityLevel;
    private String goalTimeline;
    private List<String> medicalConditions;
    private Integer dailyCalorieTarget;
    private Integer dailyProteinTarget;
    private Double bmi;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
