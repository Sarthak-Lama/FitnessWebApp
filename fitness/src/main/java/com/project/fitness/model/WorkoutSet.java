package com.project.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_workout_set_user"))
    @JsonIgnore
    private User user;

    private String exerciseName;

    @Enumerated(EnumType.STRING)
    private MuscleGroup muscleGroup;

    private Integer sets;
    private Integer reps;
    private Double weightKg;   // 0 for bodyweight exercises

    // Estimated calories burned for this exercise block
    private Double caloriesBurned;

    private Boolean isPR = false;

    private LocalDateTime loggedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
