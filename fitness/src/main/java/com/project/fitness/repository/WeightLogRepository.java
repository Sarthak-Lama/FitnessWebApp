package com.project.fitness.repository;

import com.project.fitness.model.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WeightLogRepository extends JpaRepository<WeightLog, String> {

    List<WeightLog> findByUserIdOrderByLoggedAtDesc(String userId);

    @Query("SELECT w FROM WeightLog w WHERE w.user.id = :userId AND w.loggedAt >= :start AND w.loggedAt < :end ORDER BY w.loggedAt DESC")
    List<WeightLog> findByUserIdAndDateRange(@Param("userId") String userId,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}
