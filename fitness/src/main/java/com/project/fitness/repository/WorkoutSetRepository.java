package com.project.fitness.repository;

import com.project.fitness.model.MuscleGroup;
import com.project.fitness.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, String> {

    List<WorkoutSet> findByUserIdOrderByLoggedAtDesc(String userId);

    @Query("SELECT w FROM WorkoutSet w WHERE w.user.id = :userId AND w.loggedAt >= :start AND w.loggedAt < :end ORDER BY w.loggedAt DESC")
    List<WorkoutSet> findByUserIdAndDateRange(@Param("userId") String userId,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT MAX(w.weightKg) FROM WorkoutSet w WHERE w.user.id = :userId AND LOWER(w.exerciseName) = LOWER(:exerciseName)")
    Optional<Double> findMaxWeightByUserIdAndExerciseName(@Param("userId") String userId,
                                                          @Param("exerciseName") String exerciseName);

    @Query("SELECT DISTINCT w.exerciseName FROM WorkoutSet w WHERE w.user.id = :userId")
    List<String> findDistinctExerciseNamesByUserId(@Param("userId") String userId);

    @Query("SELECT w FROM WorkoutSet w WHERE w.user.id = :userId AND LOWER(w.exerciseName) = LOWER(:exerciseName) ORDER BY w.loggedAt ASC")
    List<WorkoutSet> findByUserIdAndExerciseNameAsc(@Param("userId") String userId,
                                                    @Param("exerciseName") String exerciseName);

    @Query("SELECT w FROM WorkoutSet w WHERE w.user.id = :userId AND w.muscleGroup = :muscleGroup ORDER BY w.loggedAt DESC")
    List<WorkoutSet> findByUserIdAndMuscleGroup(@Param("userId") String userId,
                                                @Param("muscleGroup") MuscleGroup muscleGroup);
}
