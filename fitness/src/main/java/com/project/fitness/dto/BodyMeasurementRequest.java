package com.project.fitness.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BodyMeasurementRequest {
    private String userId;
    private LocalDate loggedDate;  // nullable → defaults to today
    private String unit;           // "CM" or "INCH"

    private Double chest;
    private Double waist;
    private Double hips;
    private Double leftArm;
    private Double rightArm;
    private Double leftThigh;
    private Double rightThigh;
    private Double neck;
    private String notes;
}
