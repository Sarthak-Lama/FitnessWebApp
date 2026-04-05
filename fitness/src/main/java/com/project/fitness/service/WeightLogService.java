package com.project.fitness.service;

import com.project.fitness.dto.WeightLogRequest;
import com.project.fitness.dto.WeightLogResponse;
import com.project.fitness.model.User;
import com.project.fitness.model.WeightLog;
import com.project.fitness.repository.UserRepository;
import com.project.fitness.repository.WeightLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;
    private final UserRepository userRepository;

    public WeightLogResponse logWeight(WeightLogRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        WeightLog log = WeightLog.builder()
                .user(user)
                .weightKg(request.getWeightKg())
                .notes(request.getNotes())
                .loggedAt(request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now())
                .build();

        return mapToResponse(weightLogRepository.save(log));
    }

    public List<WeightLogResponse> getUserWeightLogs(String userId) {
        return weightLogRepository.findByUserIdOrderByLoggedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private WeightLogResponse mapToResponse(WeightLog log) {
        WeightLogResponse response = new WeightLogResponse();
        response.setId(log.getId());
        response.setUserId(log.getUser().getId());
        response.setWeightKg(log.getWeightKg());
        response.setNotes(log.getNotes());
        response.setLoggedAt(log.getLoggedAt());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}
