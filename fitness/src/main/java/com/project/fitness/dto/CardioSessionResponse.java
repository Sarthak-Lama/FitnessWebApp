package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;
import com.project.fitness.model.CardioType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardioSessionResponse {
    private String id;
    private String userId;
    private ActivityType activityType;
    private CardioType cardioType;
    private Integer durationMinutes;
    private Double caloriesBurned;
    private Double distanceKm;
    private LocalDateTime loggedAt;
    private LocalDateTime createdAt;
}
