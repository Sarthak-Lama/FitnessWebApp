package com.project.fitness.repository;

import com.project.fitness.model.CardioSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CardioSessionRepository extends JpaRepository<CardioSession, String> {

    List<CardioSession> findByUserIdOrderByLoggedAtDesc(String userId);

    @Query("SELECT c FROM CardioSession c WHERE c.user.id = :userId AND c.loggedAt >= :start AND c.loggedAt < :end ORDER BY c.loggedAt DESC")
    List<CardioSession> findByUserIdAndDateRange(@Param("userId") String userId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}
