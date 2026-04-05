package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WeeklyProgressDay {
    private LocalDate date;
    private String dayLabel;        // "Mon", "Tue", …

    private double caloriesIn;
    private double caloriesOutExercise;
    private double netCalories;     // in − out

    private double proteinGrams;
    private double carbsGrams;
    private double fatGrams;

    private Double weightKg;        // null if no log for this day
    private boolean hasData;        // false if no food/exercise logged
}
