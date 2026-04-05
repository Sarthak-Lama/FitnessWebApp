package com.project.fitness.controller;

import com.project.fitness.dto.DailyCalorieSummaryResponse;
import com.project.fitness.service.CalorieSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/calorie-summary")
@RequiredArgsConstructor
public class CalorieSummaryController {

    private final CalorieSummaryService calorieSummaryService;

    /** GET /api/calorie-summary?date=2025-04-04  (date optional, defaults to today) */
    @GetMapping
    public ResponseEntity<DailyCalorieSummaryResponse> getSummary(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(calorieSummaryService.getSummary(userId, targetDate));
    }
}
