package com.project.fitness.service.recommendation;

import com.project.fitness.model.CardioSession;
import com.project.fitness.model.UserProfile;
import com.project.fitness.model.WorkoutSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates safety tips sourced from the user's gym (WorkoutSet) and
 * cardio (CardioSession) logs plus medical conditions in their profile.
 * No ActivityType dependency.
 */
@Component
public class SafetyTipGenerator {

    public List<String> generate(List<WorkoutSet> gymSets,
                                 List<CardioSession> cardioSessions,
                                 Optional<UserProfile> profileOpt) {
        List<String> out = new ArrayList<>();

        out.add("Always warm up for 5–10 minutes before exercise and cool down for 5–10 minutes after.");
        out.add("Stay hydrated — drink water before, during, and after every session.");

        if (!gymSets.isEmpty())    addGymSafety(out, gymSets);
        if (!cardioSessions.isEmpty()) addCardioSafety(out, cardioSessions);

        profileOpt.ifPresent(p -> addMedicalTips(out, p));

        out.add("Consult a healthcare professional before starting a new intense programme, especially with existing conditions.");
        return out;
    }

    private void addGymSafety(List<String> out, List<WorkoutSet> sets) {
        out.add("Never sacrifice form for heavier weights — use a spotter for maximum-effort sets.");
        out.add("Warm up each muscle group with lighter sets before moving to your working weight.");
        out.add("Stop immediately if you feel sharp joint pain — distinguish productive muscle burn from injury pain.");

        boolean hasPR = sets.stream().anyMatch(s -> Boolean.TRUE.equals(s.getIsPR()));
        if (hasPR) {
            out.add("Personal record day — allow 48–72 hours before training the same muscle group heavily again.");
        }
    }

    private void addCardioSafety(List<String> out, List<CardioSession> sessions) {
        int totalMin = sessions.stream()
                .mapToInt(c -> c.getDurationMinutes() != null ? c.getDurationMinutes() : 0).sum();

        out.add("Wear appropriate footwear with sufficient cushioning for cardio sessions.");
        if (totalMin > 60) {
            out.add("Sessions over 60 minutes — carry water and consider an electrolyte source to prevent cramping.");
        }
    }

    private void addMedicalTips(List<String> out, UserProfile profile) {
        List<String> conds = profile.getMedicalConditions();
        if (conds == null || conds.isEmpty()) return;

        if (conds.contains("HEART_CONDITION")) {
            out.add("Heart condition noted — monitor heart rate and do not push to maximum intensity without medical clearance.");
        }
        if (conds.contains("HIGH_BP")) {
            out.add("High blood pressure noted — avoid holding your breath (Valsalva manoeuvre) during heavy lifts.");
        }
        if (conds.contains("DIABETES")) {
            out.add("Diabetes noted — check blood sugar before and after exercise; carry a fast-acting carb source.");
        }
        if (conds.contains("JOINT_PAIN")) {
            out.add("Joint pain noted — warm up thoroughly and avoid exercises that aggravate specific joints.");
        }
        if (conds.contains("ASTHMA")) {
            out.add("Asthma noted — keep your inhaler accessible and avoid high-intensity cardio in cold or dry air.");
        }
    }
}
