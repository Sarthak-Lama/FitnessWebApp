package com.project.fitness.service;

import com.project.fitness.dto.UserProfileRequest;
import com.project.fitness.dto.UserProfileResponse;
import com.project.fitness.model.FitnessGoal;
import com.project.fitness.model.User;
import com.project.fitness.model.UserProfile;
import com.project.fitness.repository.UserProfileRepository;
import com.project.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileResponse saveOrUpdateProfile(UserProfileRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        UserProfile profile = userProfileRepository.findByUserId(request.getUserId())
                .orElse(UserProfile.builder().user(user).build());

        if (request.getWeightKg() != null) profile.setWeightKg(request.getWeightKg());
        if (request.getHeightCm() != null) profile.setHeightCm(request.getHeightCm());
        if (request.getTargetWeightKg() != null) profile.setTargetWeightKg(request.getTargetWeightKg());
        if (request.getAge() != null) profile.setAge(request.getAge());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getFitnessGoal() != null) profile.setFitnessGoal(request.getFitnessGoal());
        if (request.getActivityLevel() != null) profile.setActivityLevel(request.getActivityLevel());
        if (request.getGoalTimeline() != null) profile.setGoalTimeline(request.getGoalTimeline());
        if (request.getMedicalConditions() != null) profile.setMedicalConditions(request.getMedicalConditions());
        if (request.getDailyCalorieTarget() != null) {
            profile.setDailyCalorieTarget(request.getDailyCalorieTarget());
        } else if (profile.getDailyCalorieTarget() == null) {
            profile.setDailyCalorieTarget(calculateCalorieTarget(profile));
        }
        if (request.getDailyProteinTarget() != null) {
            profile.setDailyProteinTarget(request.getDailyProteinTarget());
        } else if (profile.getDailyProteinTarget() == null && profile.getWeightKg() != null) {
            profile.setDailyProteinTarget((int) (profile.getWeightKg() * 1.6));
        }

        return mapToResponse(userProfileRepository.save(profile));
    }

    public UserProfileResponse getProfile(String userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        return mapToResponse(profile);
    }

    public Optional<UserProfile> findByUserId(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    private int calculateCalorieTarget(UserProfile profile) {
        if (profile.getWeightKg() == null || profile.getHeightCm() == null || profile.getAge() == null) {
            return 2000; // default
        }
        // Mifflin-St Jeor formula (moderate activity)
        double bmr;
        if (profile.getGender() != null && profile.getGender().name().equals("FEMALE")) {
            bmr = 10 * profile.getWeightKg() + 6.25 * profile.getHeightCm() - 5 * profile.getAge() - 161;
        } else {
            bmr = 10 * profile.getWeightKg() + 6.25 * profile.getHeightCm() - 5 * profile.getAge() + 5;
        }
        double tdee = bmr * 1.55; // moderate activity factor

        if (profile.getFitnessGoal() == FitnessGoal.WEIGHT_LOSS) return (int) (tdee - 500);
        if (profile.getFitnessGoal() == FitnessGoal.WEIGHT_GAIN ||
                profile.getFitnessGoal() == FitnessGoal.MUSCLE_GAIN) return (int) (tdee + 300);
        return (int) tdee;
    }

    public UserProfileResponse mapToResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setWeightKg(profile.getWeightKg());
        response.setHeightCm(profile.getHeightCm());
        response.setTargetWeightKg(profile.getTargetWeightKg());
        response.setAge(profile.getAge());
        response.setGender(profile.getGender());
        response.setFitnessGoal(profile.getFitnessGoal());
        response.setActivityLevel(profile.getActivityLevel());
        response.setGoalTimeline(profile.getGoalTimeline());
        response.setMedicalConditions(profile.getMedicalConditions());
        response.setDailyCalorieTarget(profile.getDailyCalorieTarget());
        response.setDailyProteinTarget(profile.getDailyProteinTarget());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        if (profile.getWeightKg() != null && profile.getHeightCm() != null) {
            double heightM = profile.getHeightCm() / 100.0;
            response.setBmi(Math.round(profile.getWeightKg() / (heightM * heightM) * 10.0) / 10.0);
        }
        return response;
    }
}
