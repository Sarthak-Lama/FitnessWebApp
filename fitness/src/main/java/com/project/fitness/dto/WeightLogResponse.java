package com.project.fitness.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeightLogResponse {
    private String id;
    private String userId;
    private Double weightKg;
    private String notes;
    private LocalDateTime loggedAt;
    private LocalDateTime createdAt;
}
