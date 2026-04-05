package com.project.fitness.controller;

import com.project.fitness.dto.NextDayPlanResponse;
import com.project.fitness.service.NextDayPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/next-day-plan")
@RequiredArgsConstructor
public class NextDayPlanController {

    private final NextDayPlanService nextDayPlanService;

    /** GET /api/next-day-plan  — generate tomorrow's plan based on today's log */
    @GetMapping
    public ResponseEntity<NextDayPlanResponse> getPlan(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(nextDayPlanService.generatePlan(userId));
    }
}
