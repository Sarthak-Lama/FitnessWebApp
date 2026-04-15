package com.project.fitness.controller;

import com.project.fitness.dto.DailyRecommendationResponse;
import com.project.fitness.dto.RecommendationRequest;
import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.service.DailyRecommendationService;
import com.project.fitness.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService      recommendationService;
    private final DailyRecommendationService dailyRecommendationService;

    @PostMapping("/generate")
    public ResponseEntity<RecommendationResponse> generateRecommendation(
            @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(
                recommendationService.mapToResponse(
                        recommendationService.generateRecommendation(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationResponse>> getUserRecommendation(
            @PathVariable String userId) {
        return ResponseEntity.ok(
                recommendationService.getUserRecommendation(userId)
                        .stream()
                        .map(recommendationService::mapToResponse)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<RecommendationResponse>> getActivityRecommendation(
            @PathVariable String activityId) {
        return ResponseEntity.ok(
                recommendationService.getActivityRecommendation(activityId)
                        .stream()
                        .map(recommendationService::mapToResponse)
                        .collect(Collectors.toList()));
    }

    /**
     * Daily recommendation derived from today's gym (WorkoutSet) + cardio
     * (CardioSession) logs. Does not use the generic Activity table.
     * X-User-ID header is preferred; falls back to the JWT principal if missing.
     */
    @GetMapping("/daily")
    public ResponseEntity<DailyRecommendationResponse> getDailyRecommendation(
            @RequestHeader(value = "X-User-ID", required = false) String headerUserId,
            Authentication authentication) {
        String userId = (headerUserId != null && !headerUserId.isBlank())
                ? headerUserId
                : (authentication != null ? (String) authentication.getPrincipal() : null);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(dailyRecommendationService.generate(userId));
    }
}
