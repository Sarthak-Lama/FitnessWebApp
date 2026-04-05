package com.project.fitness.dto;

import com.project.fitness.model.MealType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodEntryResponse {
    private String id;
    private String userId;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;
    private MealType mealType;
    private LocalDateTime loggedAt;
    private LocalDateTime createdAt;
}
