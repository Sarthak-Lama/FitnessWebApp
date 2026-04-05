package com.project.fitness.service;

import com.project.fitness.dto.FoodEntryRequest;
import com.project.fitness.dto.FoodEntryResponse;
import com.project.fitness.model.FoodEntry;
import com.project.fitness.model.User;
import com.project.fitness.repository.FoodEntryRepository;
import com.project.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutritionService {

    private final FoodEntryRepository foodEntryRepository;
    private final UserRepository userRepository;

    public FoodEntryResponse logFood(FoodEntryRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        LocalDateTime loggedAt = request.getLoggedAt() != null
                ? request.getLoggedAt()
                : LocalDateTime.now();

        FoodEntry entry = FoodEntry.builder()
                .user(user)
                .foodName(request.getFoodName())
                .calories(request.getCalories())
                .protein(request.getProtein() != null ? request.getProtein() : 0.0)
                .carbs(request.getCarbs() != null ? request.getCarbs() : 0.0)
                .fat(request.getFat() != null ? request.getFat() : 0.0)
                .servingSize(request.getServingSize() != null ? request.getServingSize() : 100.0)
                .mealType(request.getMealType())
                .loggedAt(loggedAt)
                .build();

        return mapToResponse(foodEntryRepository.save(entry));
    }

    public List<FoodEntryResponse> getUserFoodEntries(String userId) {
        return foodEntryRepository.findByUserIdOrderByLoggedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodEntryResponse> getTodayFoodEntries(String userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return foodEntryRepository.findByUserIdAndDateRange(userId, startOfDay, endOfDay)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteFoodEntry(String entryId) {
        foodEntryRepository.deleteById(entryId);
    }

    public FoodEntryResponse mapToResponse(FoodEntry entry) {
        FoodEntryResponse response = new FoodEntryResponse();
        response.setId(entry.getId());
        response.setUserId(entry.getUser().getId());
        response.setFoodName(entry.getFoodName());
        response.setCalories(entry.getCalories());
        response.setProtein(entry.getProtein());
        response.setCarbs(entry.getCarbs());
        response.setFat(entry.getFat());
        response.setServingSize(entry.getServingSize());
        response.setMealType(entry.getMealType());
        response.setLoggedAt(entry.getLoggedAt());
        response.setCreatedAt(entry.getCreatedAt());
        return response;
    }
}
