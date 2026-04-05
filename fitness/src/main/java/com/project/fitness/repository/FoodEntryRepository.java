package com.project.fitness.repository;

import com.project.fitness.model.FoodEntry;
import com.project.fitness.model.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FoodEntryRepository extends JpaRepository<FoodEntry, String> {

    List<FoodEntry> findByUserIdOrderByLoggedAtDesc(String userId);

    @Query("SELECT f FROM FoodEntry f WHERE f.user.id = :userId AND f.loggedAt >= :start AND f.loggedAt < :end")
    List<FoodEntry> findByUserIdAndDateRange(@Param("userId") String userId,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    List<FoodEntry> findByUserIdAndMealType(String userId, MealType mealType);
}
