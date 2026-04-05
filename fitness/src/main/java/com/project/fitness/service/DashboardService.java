package com.project.fitness.service;

import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.dto.DashboardResponse;
import com.project.fitness.dto.FoodEntryResponse;
import com.project.fitness.model.Activity;
import com.project.fitness.model.FoodEntry;
import com.project.fitness.model.UserProfile;
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.FoodEntryRepository;
import com.project.fitness.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ActivityRepository activityRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final UserProfileRepository userProfileRepository;
    private final ActivityService activityService;
    private final NutritionService nutritionService;

    public DashboardResponse getDashboard(String userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();

        // Today's food
        List<FoodEntry> todayFood = foodEntryRepository.findByUserIdAndDateRange(userId, todayStart, todayEnd);
        double todayCalories = todayFood.stream().mapToDouble(f -> f.getCalories() != null ? f.getCalories() : 0).sum();
        double todayProtein = todayFood.stream().mapToDouble(f -> f.getProtein() != null ? f.getProtein() : 0).sum();
        double todayCarbs = todayFood.stream().mapToDouble(f -> f.getCarbs() != null ? f.getCarbs() : 0).sum();
        double todayFat = todayFood.stream().mapToDouble(f -> f.getFat() != null ? f.getFat() : 0).sum();

        // Today's activities
        List<Activity> allActivities = activityRepository.findByUserId(userId);
        List<Activity> todayActivities = allActivities.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(todayStart) && a.getCreatedAt().isBefore(todayEnd))
                .collect(Collectors.toList());
        double todayCaloriesBurned = todayActivities.stream()
                .mapToDouble(a -> a.getCaloriesBurned() != null ? a.getCaloriesBurned() : 0).sum();

        // Weekly
        List<Activity> weekActivities = allActivities.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(weekStart))
                .collect(Collectors.toList());
        double weeklyCaloriesBurned = weekActivities.stream()
                .mapToDouble(a -> a.getCaloriesBurned() != null ? a.getCaloriesBurned() : 0).sum();

        // Profile
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
        Double currentWeight = profileOpt.map(UserProfile::getWeightKg).orElse(null);
        Double targetWeight = profileOpt.map(UserProfile::getTargetWeightKg).orElse(null);
        Integer calorieTarget = profileOpt.map(UserProfile::getDailyCalorieTarget).orElse(2000);
        String goal = profileOpt.map(p -> p.getFitnessGoal() != null ? p.getFitnessGoal().name() : null).orElse(null);

        // Recent entries (last 5)
        List<ActivityResponse> recentActivities = allActivities.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(activityService::mapToResponse)
                .collect(Collectors.toList());

        List<FoodEntryResponse> recentFood = foodEntryRepository.findByUserIdOrderByLoggedAtDesc(userId)
                .stream().limit(5)
                .map(nutritionService::mapToResponse)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .todayCaloriesConsumed(Math.round(todayCalories * 10.0) / 10.0)
                .todayCaloriesBurned(Math.round(todayCaloriesBurned * 10.0) / 10.0)
                .todayProtein(Math.round(todayProtein * 10.0) / 10.0)
                .todayCarbs(Math.round(todayCarbs * 10.0) / 10.0)
                .todayFat(Math.round(todayFat * 10.0) / 10.0)
                .todayActivities(todayActivities.size())
                .weeklyCaloriesBurned(Math.round(weeklyCaloriesBurned * 10.0) / 10.0)
                .weeklyActivities(weekActivities.size())
                .currentWeight(currentWeight)
                .targetWeight(targetWeight)
                .dailyCalorieTarget(calorieTarget)
                .fitnessGoal(goal)
                .recentActivities(recentActivities)
                .recentFoodEntries(recentFood)
                .build();
    }
}
