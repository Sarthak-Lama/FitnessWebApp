package com.project.fitness.service.nextdayplan;

import com.project.fitness.dto.MealSuggestionItem;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Static catalogue of Nepali food options and selection logic.
 * Keeps all food data in one place, separate from assembly logic.
 */
@Component
public class NepaliMealDatabase {

    record MealOption(
        String mealType, String name, String nepaliName, String serving,
        double cal, double prot, double carbs, double fat,
        String tip, String[] suitableGoals
    ) {}

    private static final List<MealOption> MEAL_DB = List.of(
        // BREAKFAST ──────────────────────────────────────────────────
        new MealOption("BREAKFAST", "Beaten Rice with Curd", "Chiura + Dahi",
            "1 cup chiura + 1 cup dahi", 320, 11, 55, 6,
            "Light, easily digestible carbs with probiotic curd — ideal morning fuel.",
            new String[]{"MAINTENANCE", "WEIGHT_LOSS", "ENDURANCE", "FLEXIBILITY"}),
        new MealOption("BREAKFAST", "Egg Bhurji with Roti", "Anda Bhurji + Roti",
            "3 eggs scrambled + 2 whole wheat rotis", 420, 22, 48, 14,
            "High-protein breakfast to support muscle repair after yesterday's workout.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "MAINTENANCE"}),
        new MealOption("BREAKFAST", "Oatmeal with Banana and Milk", "Oats + Kera + Dudh",
            "1 cup oats + 1 banana + 1 cup milk", 390, 13, 68, 7,
            "Slow-release carbs and potassium — excellent pre-workout breakfast.",
            new String[]{"ENDURANCE", "WEIGHT_LOSS", "FLEXIBILITY", "MAINTENANCE"}),
        new MealOption("BREAKFAST", "Morning Dal Bhat", "Bihani Ko Dal Bhat",
            "1 cup rice + 1 cup dal + sabji", 400, 15, 70, 6,
            "A classic Nepali breakfast with complex carbs and plant protein.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "ENDURANCE", "MAINTENANCE"}),
        new MealOption("BREAKFAST", "Boiled Eggs with Chiura", "Umaleko Anda + Chiura",
            "2 boiled eggs + ½ cup chiura", 280, 15, 30, 10,
            "Protein-packed light breakfast to support fat loss while maintaining muscle.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY"}),
        new MealOption("BREAKFAST", "Wheat Porridge with Jaggery", "Gahu Ko Dhido + Chaku",
            "1 bowl wheat porridge + 1 tsp jaggery", 350, 10, 65, 5,
            "Iron-rich wholegrain porridge — keeps you full through the morning.",
            new String[]{"MAINTENANCE", "ENDURANCE", "FLEXIBILITY"}),

        // MORNING_SNACK ──────────────────────────────────────────────
        new MealOption("MORNING_SNACK", "Roasted Soybean", "Bhatmas Sadeko",
            "1 cup roasted soy", 190, 17, 14, 7,
            "One of Nepal's best plant proteins — great mid-morning protein boost.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "MAINTENANCE", "WEIGHT_LOSS", "ENDURANCE", "FLEXIBILITY"}),
        new MealOption("MORNING_SNACK", "Sweetened Curd Drink", "Lassi",
            "1 large glass (350 ml)", 180, 8, 28, 4,
            "Probiotic calcium boost — supports gut health and bone density.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "MAINTENANCE", "ENDURANCE"}),
        new MealOption("MORNING_SNACK", "Banana with Peanut Butter", "Kera + Badam Butter",
            "1 banana + 1 tbsp peanut butter", 220, 5, 38, 8,
            "Quick natural sugar with healthy fats — ideal pre-gym snack.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "ENDURANCE", "MAINTENANCE"}),
        new MealOption("MORNING_SNACK", "Curd with Honey", "Dahi + Mah",
            "1 cup curd + 1 tsp honey", 160, 7, 22, 4,
            "Light, low-calorie snack to curb hunger while in a calorie deficit.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY", "MAINTENANCE"}),
        new MealOption("MORNING_SNACK", "Sel Roti", "Sel Roti",
            "2 pieces", 220, 4, 38, 6,
            "Traditional Nepali ring bread — a carb boost if morning energy is low.",
            new String[]{"WEIGHT_GAIN", "ENDURANCE", "MAINTENANCE"}),

        // LUNCH ──────────────────────────────────────────────────────
        new MealOption("LUNCH", "Standard Dal Bhat", "Dal Bhat Tarkari",
            "2 cups rice + 1 cup dal + 1 cup sabji + salad", 650, 22, 115, 10,
            "The ultimate balanced Nepali meal — complete amino acids from rice+lentil.",
            new String[]{"MAINTENANCE", "MUSCLE_GAIN", "WEIGHT_GAIN", "ENDURANCE"}),
        new MealOption("LUNCH", "Chicken Dal Bhat", "Kukhura Ko Dal Bhat",
            "1.5 cups rice + dal + 150g chicken curry", 720, 38, 95, 15,
            "High-protein power lunch for muscle building.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN"}),
        new MealOption("LUNCH", "Moong Dal Khichdi", "Moong Dal Khichdi",
            "1.5 cups rice + 1 cup moong dal", 480, 18, 85, 8,
            "Easily digestible, low-fat meal — perfect for rest or recovery days.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY", "MAINTENANCE"}),
        new MealOption("LUNCH", "Wheat Roti with Dal and Tarkari", "Roti + Dal + Tarkari",
            "3 rotis + 1 cup dal + 1 cup tarkari", 550, 20, 92, 9,
            "Higher fibre than rice-based meals — aids digestion and extends satiety.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY", "ENDURANCE", "MAINTENANCE"}),
        new MealOption("LUNCH", "Chicken Thukpa", "Kukhura Ko Thukpa",
            "1 large bowl with egg noodles + 120g chicken", 560, 30, 72, 13,
            "Protein-rich noodle soup — excellent post-workout recovery meal.",
            new String[]{"MUSCLE_GAIN", "ENDURANCE", "MAINTENANCE"}),

        // AFTERNOON_SNACK ────────────────────────────────────────────
        new MealOption("AFTERNOON_SNACK", "Roasted Soybean with Chiura", "Bhatmas + Chiura",
            "½ cup bhatmas + ½ cup chiura", 260, 13, 40, 7,
            "Classic Nepali snack combining protein and carbs — perfect 3–4 pm energy.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "ENDURANCE", "MAINTENANCE"}),
        new MealOption("AFTERNOON_SNACK", "Steamed Momo (Chicken)", "Chicken Momo",
            "8 pieces with achar", 380, 18, 48, 10,
            "Protein-filled dumplings — treat yourself while staying on track.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "MAINTENANCE"}),
        new MealOption("AFTERNOON_SNACK", "Boiled Eggs", "Umaleko Anda",
            "2 eggs", 140, 12, 1, 10,
            "Highest-quality protein — zero carbs, keeps you full before dinner.",
            new String[]{"WEIGHT_LOSS", "MUSCLE_GAIN", "FLEXIBILITY"}),
        new MealOption("AFTERNOON_SNACK", "Fruit Salad", "Phalful Ko Salad",
            "1 bowl mixed seasonal fruits", 130, 2, 32, 1,
            "Natural vitamins and antioxidants — perfect light snack for weight loss.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY", "MAINTENANCE"}),
        new MealOption("AFTERNOON_SNACK", "Sukuti (Dried Meat)", "Sukuti",
            "50g dried buffalo meat", 160, 22, 2, 7,
            "Traditional Nepali protein snack — very high protein, nearly zero carbs.",
            new String[]{"WEIGHT_LOSS", "MUSCLE_GAIN", "FLEXIBILITY"}),
        new MealOption("AFTERNOON_SNACK", "Protein Lassi", "Malai Lassi",
            "1 large glass with thick curd", 240, 12, 32, 7,
            "Protein-rich afternoon drink — supports muscle recovery before dinner.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN", "ENDURANCE"}),

        // DINNER ─────────────────────────────────────────────────────
        new MealOption("DINNER", "Light Dal Bhat", "Halka Dal Bhat",
            "1.5 cups rice + 1 cup dal + sabji", 520, 18, 90, 8,
            "Slightly smaller than lunch — winding down calories before sleep.",
            new String[]{"MAINTENANCE", "ENDURANCE", "FLEXIBILITY"}),
        new MealOption("DINNER", "Wheat Roti with Dal and Sabji", "Roti + Dal + Sabji",
            "2 rotis + 1 cup dal + 1 cup sabji", 420, 15, 70, 9,
            "High-fibre dinner — promotes overnight digestion and better sleep quality.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY", "MAINTENANCE"}),
        new MealOption("DINNER", "Gundruk Soup with Rice", "Gundruk Ko Jhol + Bhat",
            "1 cup rice + 1 bowl gundruk soup + sabji", 380, 12, 68, 6,
            "Fermented greens rich in probiotics and calcium — light but nutritious.",
            new String[]{"WEIGHT_LOSS", "FLEXIBILITY", "MAINTENANCE"}),
        new MealOption("DINNER", "Kheer with Roti", "Kheer + Roti",
            "1 bowl kheer + 2 rotis", 560, 16, 92, 13,
            "Calorie-dense sweet dinner for weight and muscle gain goals.",
            new String[]{"WEIGHT_GAIN", "MUSCLE_GAIN"}),
        new MealOption("DINNER", "Dhido with Gundruk Ko Jhol", "Dhido + Gundruk Ko Jhol",
            "1 bowl dhido + 1 bowl gundruk soup", 440, 13, 80, 7,
            "Traditional buckwheat/millet dhido — high in magnesium, great for recovery.",
            new String[]{"ENDURANCE", "MAINTENANCE", "FLEXIBILITY"}),
        new MealOption("DINNER", "Muscle Gain Dal Bhat", "Protein Dal Bhat",
            "2 cups rice + 1 cup dal + 150g chicken + sabji", 780, 40, 108, 14,
            "High-calorie, high-protein dinner to fuel overnight muscle protein synthesis.",
            new String[]{"MUSCLE_GAIN", "WEIGHT_GAIN"})
    );

    /**
     * Returns the best-matching meal for the given type, goal and calorie target.
     * Goal-appropriate options are preferred; closest calorie match is the tiebreaker.
     */
    public MealSuggestionItem selectBest(String mealType, String goal, double targetCal) {
        List<MealOption> candidates = MEAL_DB.stream()
                .filter(m -> m.mealType().equals(mealType))
                .sorted(Comparator.comparingDouble(m -> Math.abs(m.cal() - targetCal)))
                .collect(Collectors.toList());

        MealOption chosen = candidates.stream()
                .filter(m -> Arrays.asList(m.suitableGoals()).contains(goal))
                .findFirst()
                .orElse(candidates.isEmpty() ? null : candidates.get(0));

        if (chosen == null) {
            return MealSuggestionItem.builder()
                    .mealType(mealType).foodName("Dal Bhat").nepaliName("Dal Bhat")
                    .servingSize("Standard serving").calories(targetCal)
                    .proteinGrams(15).carbsGrams(60).fatGrams(8)
                    .tip("Classic Nepali staple — always a balanced choice.").build();
        }
        return MealSuggestionItem.builder()
                .mealType(chosen.mealType()).foodName(chosen.name()).nepaliName(chosen.nepaliName())
                .servingSize(chosen.serving()).calories(chosen.cal())
                .proteinGrams(chosen.prot()).carbsGrams(chosen.carbs()).fatGrams(chosen.fat())
                .tip(chosen.tip()).build();
    }
}
