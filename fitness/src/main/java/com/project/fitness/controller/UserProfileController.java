package com.project.fitness.controller;

import com.project.fitness.dto.UserProfileRequest;
import com.project.fitness.dto.UserProfileResponse;
import com.project.fitness.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileResponse> saveOrUpdateProfile(
            @Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.saveOrUpdateProfile(request));
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader("X-User-ID") String userId) {
        try {
            return ResponseEntity.ok(userProfileService.getProfile(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
