package com.project.fitness.service;

import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.model.User;
import com.project.fitness.model.UserProfile;
import com.project.fitness.model.UserRole;
import com.project.fitness.repository.UserProfileRepository;
import com.project.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        UserRole role = request.getRole() != null ? request.getRole() : UserRole.USER;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User savedUser = userRepository.save(user);

        // Auto-create UserProfile from registration data
        if (request.getWeightKg() != null || request.getHeightCm() != null || request.getAge() != null) {
            UserProfile profile = UserProfile.builder()
                    .user(savedUser)
                    .weightKg(request.getWeightKg())
                    .heightCm(request.getHeightCm())
                    .targetWeightKg(request.getTargetWeightKg())
                    .age(request.getAge())
                    .gender(request.getGender())
                    .fitnessGoal(request.getFitnessGoal())
                    .activityLevel(request.getActivityLevel())
                    .goalTimeline(request.getGoalTimeline())
                    .medicalConditions(request.getMedicalConditions())
                    .build();
            userProfileRepository.save(profile);
        }

        return mapToResponse(savedUser);
    }

    public UserResponse mapToResponse(User savedUser) {
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        return response;
    }

    public User authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) throw new RuntimeException("Invalid credentials");
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");
        return user;
    }
}
