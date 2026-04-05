package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealSuggestionItem {
    private String mealType;      // BREAKFAST, MORNING_SNACK, LUNCH, AFTERNOON_SNACK, DINNER
    private String foodName;
    private String nepaliName;
    private String servingSize;
    private double calories;
    private double proteinGrams;
    private double carbsGrams;
    private double fatGrams;
    private String tip;           // why this food was chosen
}
