package com.project.fitness.service.weeklyprogress;

import com.project.fitness.dto.WeeklyProgressDay;
import com.project.fitness.dto.WeeklyProgressResponse;
import com.project.fitness.model.CardioSession;
import com.project.fitness.model.WeightLog;
import com.project.fitness.model.WorkoutSet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Computes weekly aggregates (averages, totals, weight change, session counts)
 * from the per-day list and raw entity data. Pure logic — no repository access.
 */
@Component
public class WeeklySummaryBuilder {

    public WeeklyProgressResponse build(List<WeeklyProgressDay> days, WeekData data) {
        List<WeeklyProgressDay> withData = days.stream().filter(WeeklyProgressDay::isHasData).toList();

        // Averages
        double avgCalIn  = avg(withData, WeeklyProgressDay::getCaloriesIn);
        double avgCalOut = avg(withData, WeeklyProgressDay::getCaloriesOutExercise);
        double avgProt   = avg(withData, WeeklyProgressDay::getProteinGrams);
        double avgCarbs  = avg(withData, WeeklyProgressDay::getCarbsGrams);
        double avgFat    = avg(withData, WeeklyProgressDay::getFatGrams);

        // Weight change
        List<WeightLog> sortedWeights = data.weights().stream()
                .sorted(Comparator.comparing(WeightLog::getLoggedAt)).toList();
        Double startW  = sortedWeights.isEmpty() ? null : sortedWeights.get(0).getWeightKg();
        Double endW    = sortedWeights.isEmpty() ? null : sortedWeights.get(sortedWeights.size() - 1).getWeightKg();
        Double wChange = (startW != null && endW != null && !startW.equals(endW))
                ? round(endW - startW) : null;

        // Totals
        double totalBurn = data.sets().stream().mapToDouble(w -> orZero(w.getCaloriesBurned())).sum()
                         + data.cardio().stream().mapToDouble(c -> orZero(c.getCaloriesBurned())).sum();

        // Session counts (distinct days with activity)
        Map<LocalDate, List<WorkoutSet>>    setsByDay   = data.sets().stream().collect(Collectors.groupingBy(w -> w.getLoggedAt().toLocalDate()));
        Map<LocalDate, List<CardioSession>> cardioByDay = data.cardio().stream().collect(Collectors.groupingBy(c -> c.getLoggedAt().toLocalDate()));
        int gymDays   = (int) setsByDay.values().stream().filter(l -> !l.isEmpty()).count();
        int cardioDays= (int) cardioByDay.values().stream().filter(l -> !l.isEmpty()).count();

        return WeeklyProgressResponse.builder()
                .days(days)
                .avgCaloriesIn(round(avgCalIn))
                .avgCaloriesOutExercise(round(avgCalOut))
                .avgProtein(round(avgProt))
                .avgCarbs(round(avgCarbs))
                .avgFat(round(avgFat))
                .startWeight(startW).endWeight(endW).weekWeightChange(wChange)
                .totalExerciseCalories(round(totalBurn))
                .totalGymSessions(gymDays)
                .totalCardioSessions(cardioDays)
                .build();
    }

    private double avg(List<WeeklyProgressDay> days,
                       java.util.function.ToDoubleFunction<WeeklyProgressDay> fn) {
        return days.isEmpty() ? 0 : days.stream().mapToDouble(fn).average().orElse(0);
    }

    private double orZero(Double v) { return v != null ? v : 0.0; }
    private double round(double v)  { return Math.round(v * 10.0) / 10.0; }
}
