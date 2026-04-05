package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BodyMeasurementResponse {
    private String id;
    private LocalDate loggedDate;

    // Values in cm
    private Double chestCm;
    private Double waistCm;
    private Double hipsCm;
    private Double leftArmCm;
    private Double rightArmCm;
    private Double leftThighCm;
    private Double rightThighCm;
    private Double neckCm;

    // Change vs previous entry (null for first entry)
    private Double chestChange;
    private Double waistChange;
    private Double hipsChange;
    private Double leftArmChange;
    private Double rightArmChange;
    private Double leftThighChange;
    private Double rightThighChange;
    private Double neckChange;

    private String notes;
}
