package com.project.fitness.dto;

import com.project.fitness.model.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodEntryRequest {

    @NotNull
    private String userId;

    @NotBlank
    private String foodName;

    @NotNull
    @Positive
    private Double calories;

    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;

    @NotNull
    private MealType mealType;

    private LocalDateTime loggedAt;
}
