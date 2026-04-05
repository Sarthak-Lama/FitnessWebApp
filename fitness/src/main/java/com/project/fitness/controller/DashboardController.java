package com.project.fitness.controller;

import com.project.fitness.dto.DashboardResponse;
import com.project.fitness.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(dashboardService.getDashboard(userId));
    }
}
