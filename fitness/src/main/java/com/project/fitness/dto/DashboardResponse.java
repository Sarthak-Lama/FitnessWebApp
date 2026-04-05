package com.project.fitness.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {
    // Today's stats
    private Double todayCaloriesConsumed;
    private Double todayCaloriesBurned;
    private Double todayProtein;
    private Double todayCarbs;
    private Double todayFat;
    private Integer todayActivities;

    // Weekly stats
    private Double weeklyCaloriesBurned;
    private Integer weeklyActivities;

    // Profile info
    private Double currentWeight;
    private Double targetWeight;
    private Integer dailyCalorieTarget;
    private String fitnessGoal;

    // Recent entries
    private List<ActivityResponse> recentActivities;
    private List<FoodEntryResponse> recentFoodEntries;
}
