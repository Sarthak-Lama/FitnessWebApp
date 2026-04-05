package com.project.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSuggestion {
    private String mealType;   // BREAKFAST, LUNCH, DINNER, SNACK
    private String foodName;
    private int    calories;
    private double proteinG;
    private double carbsG;
    private double fatG;
    private String tip;
}
