package com.project.fitness.service;

import com.project.fitness.dto.BodyMeasurementRequest;
import com.project.fitness.dto.BodyMeasurementResponse;
import com.project.fitness.model.BodyMeasurement;
import com.project.fitness.model.User;
import com.project.fitness.repository.BodyMeasurementRepository;
import com.project.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyMeasurementService {

    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final UserRepository userRepository;

    private static final double INCH_TO_CM = 2.54;

    public BodyMeasurementResponse logMeasurement(BodyMeasurementRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isInch = "INCH".equalsIgnoreCase(req.getUnit());

        BodyMeasurement m = BodyMeasurement.builder()
                .user(user)
                .loggedDate(req.getLoggedDate() != null ? req.getLoggedDate() : LocalDate.now())
                .chestCm(toCm(req.getChest(), isInch))
                .waistCm(toCm(req.getWaist(), isInch))
                .hipsCm(toCm(req.getHips(), isInch))
                .leftArmCm(toCm(req.getLeftArm(), isInch))
                .rightArmCm(toCm(req.getRightArm(), isInch))
                .leftThighCm(toCm(req.getLeftThigh(), isInch))
                .rightThighCm(toCm(req.getRightThigh(), isInch))
                .neckCm(toCm(req.getNeck(), isInch))
                .notes(req.getNotes())
                .build();

        bodyMeasurementRepository.save(m);
        return buildResponse(m, null);
    }

    public List<BodyMeasurementResponse> getMeasurements(String userId) {
        List<BodyMeasurement> logs = bodyMeasurementRepository.findByUserIdOrderByLoggedDateDesc(userId);
        List<BodyMeasurementResponse> result = new ArrayList<>();
        for (int i = 0; i < logs.size(); i++) {
            BodyMeasurement curr = logs.get(i);
            BodyMeasurement prev = (i + 1 < logs.size()) ? logs.get(i + 1) : null;
            result.add(buildResponse(curr, prev));
        }
        return result;
    }

    // ── Helpers ──────────────────────────────────────────
    private BodyMeasurementResponse buildResponse(BodyMeasurement curr, BodyMeasurement prev) {
        return BodyMeasurementResponse.builder()
                .id(curr.getId())
                .loggedDate(curr.getLoggedDate())
                .chestCm(curr.getChestCm())
                .waistCm(curr.getWaistCm())
                .hipsCm(curr.getHipsCm())
                .leftArmCm(curr.getLeftArmCm())
                .rightArmCm(curr.getRightArmCm())
                .leftThighCm(curr.getLeftThighCm())
                .rightThighCm(curr.getRightThighCm())
                .neckCm(curr.getNeckCm())
                .chestChange(diff(curr.getChestCm(), prev != null ? prev.getChestCm() : null))
                .waistChange(diff(curr.getWaistCm(), prev != null ? prev.getWaistCm() : null))
                .hipsChange(diff(curr.getHipsCm(), prev != null ? prev.getHipsCm() : null))
                .leftArmChange(diff(curr.getLeftArmCm(), prev != null ? prev.getLeftArmCm() : null))
                .rightArmChange(diff(curr.getRightArmCm(), prev != null ? prev.getRightArmCm() : null))
                .leftThighChange(diff(curr.getLeftThighCm(), prev != null ? prev.getLeftThighCm() : null))
                .rightThighChange(diff(curr.getRightThighCm(), prev != null ? prev.getRightThighCm() : null))
                .neckChange(diff(curr.getNeckCm(), prev != null ? prev.getNeckCm() : null))
                .notes(curr.getNotes())
                .build();
    }

    private Double toCm(Double val, boolean isInch) {
        if (val == null) return null;
        return isInch ? round(val * INCH_TO_CM) : round(val);
    }

    private Double diff(Double curr, Double prev) {
        if (curr == null || prev == null) return null;
        return round(curr - prev);
    }

    private double round(double v) { return Math.round(v * 10.0) / 10.0; }
}
