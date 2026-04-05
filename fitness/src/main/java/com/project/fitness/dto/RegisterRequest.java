package com.project.fitness.dto;

import com.project.fitness.model.ActivityLevel;
import com.project.fitness.model.FitnessGoal;
import com.project.fitness.model.Gender;
import com.project.fitness.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    // --- Account ---
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String firstName;
    private String lastName;
    private UserRole role;

    // --- Personal ---
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;

    private Gender gender;

    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm")
    @DecimalMax(value = "250.0", message = "Height must be at most 250 cm")
    private Double heightCm;

    @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
    @DecimalMax(value = "300.0", message = "Weight must be at most 300 kg")
    private Double weightKg;

    // --- Health ---
    private List<String> medicalConditions;
    private ActivityLevel activityLevel;

    // --- Goals ---
    @DecimalMin(value = "20.0", message = "Target weight must be at least 20 kg")
    @DecimalMax(value = "300.0", message = "Target weight must be at most 300 kg")
    private Double targetWeightKg;

    private FitnessGoal fitnessGoal;
    private String goalTimeline;
}
