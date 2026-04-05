package com.project.fitness.service.nextdayplan;

import com.project.fitness.dto.DailyCalorieSummaryResponse;
import com.project.fitness.model.ActivityLevel;
import com.project.fitness.model.UserProfile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the reminder list and the overall motivational message
 * for the next-day plan. No external dependencies — receives only
 * pre-computed values from the orchestrator.
 */
@Component
public class ReminderBuilder {

    public List<String> buildReminders(UserProfile profile, DailyCalorieSummaryResponse summary,
                                       int consecGym, boolean restDay) {
        List<String> reminders = new ArrayList<>();

        addProteinReminder(reminders, summary);
        addHydrationReminder(reminders, profile, summary);
        addSleepReminder(reminders, consecGym, summary);
        addOvertrainingWarning(reminders, restDay, consecGym);
        addMedicalReminders(reminders, profile);
        addBalanceReminder(reminders, summary);

        return reminders;
    }

    public String buildOverallMessage(String goal, DailyCalorieSummaryResponse summary,
                                      boolean restDay, int consecGym, double tomorrowTarget) {
        if (restDay) {
            return String.format(
                "You've trained hard for %d consecutive days — great dedication! " +
                "Tomorrow is a planned rest day. Your muscles grow during rest, not during the workout itself. " +
                "Light movement, good food, plenty of sleep, and water is all you need.", consecGym);
        }

        String emoji = switch (summary.getBalanceStatus()) {
            case "ON_TRACK" -> "🎯";
            case "SURPLUS"  -> "📈";
            default         -> "📉";
        };

        return emoji + " Today's balance: " + summary.getBalanceStatus() + ". " + switch (goal) {
            case "MUSCLE_GAIN", "WEIGHT_GAIN" -> String.format(
                "Your tomorrow target is %.0f kcal — focus on hitting protein first, " +
                "then fill remaining calories with complex carbs and healthy fats. " +
                "The gym session targets a new muscle group to keep the stimulus fresh.", tomorrowTarget);
            case "WEIGHT_LOSS" -> String.format(
                "Your tomorrow target is %.0f kcal. Stay consistent — sustainable weight loss " +
                "is 0.5–1 kg per week. Don't skip meals — it leads to binge eating.", tomorrowTarget);
            case "ENDURANCE" -> String.format(
                "Your tomorrow target is %.0f kcal with a carb-forward meal plan to fuel " +
                "your cardio session. Carbs are your primary fuel source.", tomorrowTarget);
            default -> String.format(
                "Your tomorrow target is %.0f kcal. The plan below keeps your macros " +
                "balanced for sustained energy and overall health.", tomorrowTarget);
        };
    }

    // ── Private reminder helpers ──────────────────────────
    private void addProteinReminder(List<String> r, DailyCalorieSummaryResponse summary) {
        if (summary.getProteinGrams() < summary.getProteinTargetGrams() * 0.80) {
            double gap = Math.round(summary.getProteinTargetGrams() - summary.getProteinGrams());
            r.add(String.format("💪 You were %.0fg short of your protein target today. " +
                "Tomorrow prioritise dal, eggs, bhatmas, or sukuti to hit your goal.", gap));
        }
    }

    private void addHydrationReminder(List<String> r, UserProfile profile,
                                      DailyCalorieSummaryResponse summary) {
        boolean heavySweat = summary.getCaloriesBurnedExercise() > 300
            || (profile != null && profile.getActivityLevel() == ActivityLevel.VERY_ACTIVE);
        if (heavySweat) {
            r.add("💧 High exercise output today — aim for at least 3 litres of water tomorrow. " +
                "Add a pinch of salt to a glass of water to replace electrolytes.");
        } else {
            r.add("💧 Stay hydrated — drink at least 2.5 litres of water throughout the day. " +
                "Start your morning with a glass of lukewarm water.");
        }
    }

    private void addSleepReminder(List<String> r, int consecGym,
                                  DailyCalorieSummaryResponse summary) {
        if (consecGym >= 2 || summary.getCaloriesBurnedExercise() > 400) {
            r.add("😴 You trained hard recently. Aim for 7–9 hours of sleep tonight. " +
                "Growth hormone is released during deep sleep — this is when muscles actually grow.");
        } else {
            r.add("😴 Aim for 7–8 hours of sleep. Consistent sleep improves metabolism, " +
                "mood, and recovery — all critical for your fitness goals.");
        }
    }

    private void addOvertrainingWarning(List<String> r, boolean restDay, int consecGym) {
        if (!restDay && consecGym == 2) {
            r.add("⚠️ You've trained 2 consecutive days. If you gym again tomorrow, the day after " +
                "should be a full rest or light-activity day to prevent overtraining.");
        }
    }

    private void addMedicalReminders(List<String> r, UserProfile profile) {
        if (profile == null || profile.getMedicalConditions() == null) return;
        if (profile.getMedicalConditions().contains("DIABETES")) {
            r.add("🩺 Diabetes reminder: keep meal timings consistent and avoid high-sugar " +
                "foods in the evening. Dal, vegetables, and lean protein are your best friends.");
        }
        if (profile.getMedicalConditions().contains("HIGH_BP")) {
            r.add("🩺 Blood pressure reminder: limit salt tomorrow. Choose gundruk over fried " +
                "snacks, and avoid high-sodium processed foods.");
        }
        if (profile.getMedicalConditions().contains("KNEE_PAIN")) {
            r.add("🩺 Knee pain reminder: avoid deep squats and high-impact jumping tomorrow. " +
                "Substitute with leg press, step-ups, and swimming if available.");
        }
    }

    private void addBalanceReminder(List<String> r, DailyCalorieSummaryResponse summary) {
        if (Math.abs(summary.getDeficitSurplus()) > 500) {
            String direction = summary.getDeficitSurplus() > 0 ? "overate" : "under-ate";
            r.add(String.format("📊 You %s by %.0f kcal today. Tomorrow's meal plan above " +
                "already adjusts for this — stick to it for smooth progress.",
                direction, Math.abs(summary.getDeficitSurplus())));
        }
    }
}
