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
public class CardioSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cardio_user"))
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;   // RUNNING, WALKING, CYCLING …

    @Enumerated(EnumType.STRING)
    private CardioType cardioType;       // TREADMILL or OUTDOOR

    private Integer durationMinutes;
    private Double caloriesBurned;
    private Double distanceKm;

    private LocalDateTime loggedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
