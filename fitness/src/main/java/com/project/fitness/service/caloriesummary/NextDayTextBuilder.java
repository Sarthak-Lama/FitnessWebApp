package com.project.fitness.service.caloriesummary;

import com.project.fitness.model.UserProfile;
import org.springframework.stereotype.Component;

/**
 * Builds the human-readable next-day recommendation text shown in the
 * calorie summary response. No external dependencies.
 */
@Component
public class NextDayTextBuilder {

    /**
     * Generates a personalised recommendation for the next day.
     *
     * @param todayDefSurp     positive = surplus, negative = deficit
     * @param yesterdayDefSurp nullable; same sign convention
     * @param profile          nullable
     * @param calIn            today's calories consumed
     * @param goal             today's daily calorie goal
     */
    public String build(double todayDefSurp, Double yesterdayDefSurp,
                        UserProfile profile, double calIn, double goal) {
        StringBuilder sb = new StringBuilder();
        String goalStr = goalName(profile);

        if (Math.abs(todayDefSurp) <= 150) {
            sb.append("✅ Great job today — you hit your calorie target almost perfectly. ");
            sb.append("Keep the same intake tomorrow.");
        } else if (todayDefSurp > 0) {
            appendSurplusAdvice(sb, todayDefSurp, goalStr);
        } else {
            appendDeficitAdvice(sb, todayDefSurp, goalStr);
        }

        appendCumulativeNote(sb, todayDefSurp, yesterdayDefSurp);
        return sb.toString();
    }

    // ── Private helpers ───────────────────────────────────
    private void appendSurplusAdvice(StringBuilder sb, double surplus, String goalStr) {
        double overBy = Math.round(surplus);
        sb.append(String.format("⚠️ You exceeded your goal by %.0f kcal today. ", overBy));

        switch (goalStr) {
            case "WEIGHT_LOSS" -> {
                sb.append(String.format(
                    "Tomorrow, aim to reduce intake by ~%.0f kcal to compensate. ",
                    Math.min(overBy * 0.5, 300)));
                sb.append("Consider replacing sel roti or jeri with gundruk soup or a fruit.");
            }
            case "MUSCLE_GAIN", "WEIGHT_GAIN" ->
                sb.append("This is fine for your muscle-gain goal — just ensure the extra calories came from protein and complex carbs.");
            default ->
                sb.append("Try to reduce intake by roughly half that amount tomorrow to stay on track.");
        }
    }

    private void appendDeficitAdvice(StringBuilder sb, double deficit, String goalStr) {
        double underBy = Math.round(-deficit);
        sb.append(String.format("📉 You ate %.0f kcal below your target today. ", underBy));

        switch (goalStr) {
            case "WEIGHT_LOSS" -> {
                if (underBy > 700) {
                    sb.append("This deficit is too large — it may cause muscle loss and fatigue. ");
                    sb.append("Tomorrow, add a protein-rich snack like bhatmas (roasted soy), a boiled egg, or curd with chiura.");
                } else {
                    sb.append("Excellent for weight loss! Tomorrow maintain the same pattern. ");
                    sb.append("Ensure adequate protein (dal, sukuti, eggs) to preserve muscle.");
                }
            }
            case "MUSCLE_GAIN", "WEIGHT_GAIN" -> {
                sb.append(String.format(
                    "For muscle gain, you need a surplus. Tomorrow add ~%.0f kcal through an extra meal. ", underBy + 100));
                sb.append("Good options: kheer, dal bhat with extra ghee, or a glass of lassi with peanut butter.");
            }
            default -> sb.append("Tomorrow, add a balanced snack to make up the shortfall.");
        }
    }

    private void appendCumulativeNote(StringBuilder sb, double today, Double yesterday) {
        if (yesterday == null || Math.abs(yesterday) <= 200) return;
        double cumulative = yesterday + today;
        if (Math.abs(cumulative) > 400) {
            sb.append(String.format(
                " Your 2-day cumulative balance is %.0f kcal — keep this in mind for planning your meals this week.",
                cumulative));
        }
    }

    private String goalName(UserProfile p) {
        return (p != null && p.getFitnessGoal() != null) ? p.getFitnessGoal().name() : "MAINTENANCE";
    }
}
