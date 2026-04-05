package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;
import com.project.fitness.model.CardioType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardioSessionRequest {
    @NotNull
    private String userId;

    @NotNull
    private ActivityType activityType;

    @NotNull
    private CardioType cardioType;

    @NotNull
    private Integer durationMinutes;

    private Double distanceKm;
    private Double caloriesBurned;  // client-calculated, backend can override
    private LocalDateTime loggedAt;
}
