package com.project.fitness.service;

import com.project.fitness.dto.RecommendationRequest;
import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.model.*;
import com.project.fitness.repository.*;
import com.project.fitness.service.recommendation.ImprovementGenerator;
import com.project.fitness.service.recommendation.SafetyTipGenerator;
import com.project.fitness.service.recommendation.SuggestionGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates recommendation generation.
 * Content is derived exclusively from the user's gym (WorkoutSet) and
 * cardio (CardioSession) logs — never from the ActivityType enum.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository            userRepository;
    private final ActivityRepository        activityRepository;
    private final RecommendationRepository  recommendationRepository;
    private final UserProfileRepository     userProfileRepository;
    private final WorkoutSetRepository      workoutSetRepository;
    private final CardioSessionRepository   cardioSessionRepository;

    private final ImprovementGenerator improvementGenerator;
    private final SuggestionGenerator  suggestionGenerator;
    private final SafetyTipGenerator   safetyTipGenerator;

    public Recommendation generateRecommendation(RecommendationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found: " + request.getActivityId()));

        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(request.getUserId());

        // Source: today's gym + cardio logs — no ActivityType used
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end   = start.plusDays(1);
        List<WorkoutSet>    gymSets  = workoutSetRepository.findByUserIdAndDateRange(request.getUserId(), start, end);
        List<CardioSession> cardioSess = cardioSessionRepository.findByUserIdAndDateRange(request.getUserId(), start, end);

        Recommendation recommendation = Recommendation.builder()
                .user(user)
                .activity(activity)
                .improvements(improvementGenerator.generate(gymSets, cardioSess, profileOpt))
                .suggestions(suggestionGenerator.generate(gymSets, cardioSess, profileOpt))
                .safety(safetyTipGenerator.generate(gymSets, cardioSess, profileOpt))
                .build();

        return recommendationRepository.save(recommendation);
    }

    public List<Recommendation> getUserRecommendation(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public List<Recommendation> getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId);
    }

    public RecommendationResponse mapToResponse(Recommendation rec) {
        RecommendationResponse r = new RecommendationResponse();
        r.setId(rec.getId());
        r.setUserId(rec.getUser().getId());
        r.setActivityId(rec.getActivity().getId());
        // activityType intentionally omitted — recommendations are gym/cardio-sourced
        r.setImprovements(rec.getImprovements());
        r.setSuggestions(rec.getSuggestions());
        r.setSafety(rec.getSafety());
        r.setCreatedAt(rec.getCreatedAt());
        r.setUpdatedAt(rec.getUpdatedAt());
        return r;
    }
}
