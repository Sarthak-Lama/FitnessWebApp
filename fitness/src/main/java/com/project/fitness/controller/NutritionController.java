package com.project.fitness.controller;

import com.project.fitness.dto.FoodEntryRequest;
import com.project.fitness.dto.FoodEntryResponse;
import com.project.fitness.service.NutritionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    @PostMapping
    public ResponseEntity<FoodEntryResponse> logFood(@Valid @RequestBody FoodEntryRequest request) {
        return ResponseEntity.ok(nutritionService.logFood(request));
    }

    @GetMapping
    public ResponseEntity<List<FoodEntryResponse>> getAllFoodEntries(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(nutritionService.getUserFoodEntries(userId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<FoodEntryResponse>> getTodayFoodEntries(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(nutritionService.getTodayFoodEntries(userId));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteFoodEntry(@PathVariable String entryId) {
        nutritionService.deleteFoodEntry(entryId);
        return ResponseEntity.noContent().build();
    }
}
