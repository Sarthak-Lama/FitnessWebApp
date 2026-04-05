package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeeklyProgressResponse {
    private List<WeeklyProgressDay> days;      // 7 entries, oldest first

    // Week averages (only days with data)
    private double avgCaloriesIn;
    private double avgCaloriesOutExercise;
    private double avgProtein;
    private double avgCarbs;
    private double avgFat;

    // Weight summary
    private Double startWeight;               // oldest log in the week
    private Double endWeight;                 // most recent log in the week
    private Double weekWeightChange;          // end − start (null if < 2 logs)

    // Total exercise calories for the week
    private double totalExerciseCalories;
    private int totalGymSessions;
    private int totalCardioSessions;
}
