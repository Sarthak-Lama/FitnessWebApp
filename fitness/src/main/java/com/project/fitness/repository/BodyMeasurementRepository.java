package com.project.fitness.repository;

import com.project.fitness.model.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {

    List<BodyMeasurement> findByUserIdOrderByLoggedDateDesc(String userId);
}
