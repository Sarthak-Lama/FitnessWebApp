package com.project.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_measurement_user"))
    @JsonIgnore
    private User user;

    private LocalDate loggedDate;   // date of measurement

    // All stored in centimetres regardless of input unit
    private Double chestCm;
    private Double waistCm;
    private Double hipsCm;
    private Double leftArmCm;
    private Double rightArmCm;
    private Double leftThighCm;
    private Double rightThighCm;
    private Double neckCm;

    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
