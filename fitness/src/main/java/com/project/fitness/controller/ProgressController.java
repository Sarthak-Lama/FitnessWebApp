package com.project.fitness.controller;

import com.project.fitness.dto.*;
import com.project.fitness.service.BodyMeasurementService;
import com.project.fitness.service.WeeklyProgressService;
import com.project.fitness.service.WeightLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final WeightLogService         weightLogService;
    private final BodyMeasurementService   bodyMeasurementService;
    private final WeeklyProgressService    weeklyProgressService;

    // ── Weight ────────────────────────────────────────────
    @PostMapping("/weight")
    public ResponseEntity<WeightLogResponse> logWeight(@Valid @RequestBody WeightLogRequest request) {
        return ResponseEntity.ok(weightLogService.logWeight(request));
    }

    @GetMapping("/weight")
    public ResponseEntity<List<WeightLogResponse>> getWeightLogs(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(weightLogService.getUserWeightLogs(userId));
    }

    // ── Body measurements ─────────────────────────────────
    @PostMapping("/measurements")
    public ResponseEntity<BodyMeasurementResponse> logMeasurement(
            @RequestBody BodyMeasurementRequest request) {
        return ResponseEntity.ok(bodyMeasurementService.logMeasurement(request));
    }

    @GetMapping("/measurements")
    public ResponseEntity<List<BodyMeasurementResponse>> getMeasurements(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(bodyMeasurementService.getMeasurements(userId));
    }

    // ── Weekly progress ───────────────────────────────────
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyProgressResponse> getWeeklyProgress(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(weeklyProgressService.getWeekly(userId));
    }
}
