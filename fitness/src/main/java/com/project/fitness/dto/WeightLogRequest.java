package com.project.fitness.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeightLogRequest {

    @NotNull
    private String userId;

    @NotNull
    @Positive
    private Double weightKg;

    private String notes;
    private LocalDateTime loggedAt;
}
