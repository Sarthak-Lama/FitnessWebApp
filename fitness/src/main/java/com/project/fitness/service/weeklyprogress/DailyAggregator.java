package com.project.fitness.service.weeklyprogress;

import com.project.fitness.dto.WeeklyProgressDay;
import com.project.fitness.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts raw entity lists into per-day {@link WeeklyProgressDay} objects.
 * Pure logic — no repository access.
 */
@Component
public class DailyAggregator {

    /**
     * Builds one {@link WeeklyProgressDay} for each date in the 7-day window
     * using pre-fetched data grouped by date.
     */
    public List<WeeklyProgressDay> buildDays(LocalDate today, WeekData data) {
        Map<LocalDate, List<FoodEntry>>     foodByDay   = groupByDate(data.food(),   f -> f.getLoggedAt().toLocalDate());
        Map<LocalDate, List<WorkoutSet>>    setsByDay   = groupByDate(data.sets(),   w -> w.getLoggedAt().toLocalDate());
        Map<LocalDate, List<CardioSession>> cardioByDay = groupByDate(data.cardio(), c -> c.getLoggedAt().toLocalDate());
        Map<LocalDate, List<WeightLog>>     weightByDay = groupByDate(data.weights(),w -> w.getLoggedAt().toLocalDate());

        List<WeeklyProgressDay> days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            days.add(buildOneDay(today.minusDays(i), foodByDay, setsByDay, cardioByDay, weightByDay));
        }
        return days;
    }

    private WeeklyProgressDay buildOneDay(LocalDate day,
            Map<LocalDate, List<FoodEntry>>     foodByDay,
            Map<LocalDate, List<WorkoutSet>>    setsByDay,
            Map<LocalDate, List<CardioSession>> cardioByDay,
            Map<LocalDate, List<WeightLog>>     weightByDay) {

        List<FoodEntry>     food   = foodByDay.getOrDefault(day, List.of());
        List<WorkoutSet>    sets   = setsByDay.getOrDefault(day, List.of());
        List<CardioSession> cardio = cardioByDay.getOrDefault(day, List.of());
        List<WeightLog>     wts    = weightByDay.getOrDefault(day, List.of());

        double calIn  = round(food.stream().mapToDouble(f -> orZero(f.getCalories())).sum());
        double prot   = round(food.stream().mapToDouble(f -> orZero(f.getProtein())).sum());
        double carbs  = round(food.stream().mapToDouble(f -> orZero(f.getCarbs())).sum());
        double fat    = round(food.stream().mapToDouble(f -> orZero(f.getFat())).sum());
        double calOut = round(
            sets.stream().mapToDouble(w -> orZero(w.getCaloriesBurned())).sum() +
            cardio.stream().mapToDouble(c -> orZero(c.getCaloriesBurned())).sum()
        );

        String label = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        return WeeklyProgressDay.builder()
                .date(day).dayLabel(label)
                .caloriesIn(calIn).caloriesOutExercise(calOut).netCalories(round(calIn - calOut))
                .proteinGrams(prot).carbsGrams(carbs).fatGrams(fat)
                .weightKg(wts.isEmpty() ? null : wts.get(0).getWeightKg())
                .hasData(!food.isEmpty() || !sets.isEmpty() || !cardio.isEmpty())
                .build();
    }

    private <T> Map<LocalDate, List<T>> groupByDate(List<T> items, Function<T, LocalDate> key) {
        return items.stream().collect(Collectors.groupingBy(key));
    }

    private double orZero(Double v) { return v != null ? v : 0.0; }
    private double round(double v)  { return Math.round(v * 10.0) / 10.0; }
}
