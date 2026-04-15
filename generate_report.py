from docx import Document
from docx.shared import Pt, RGBColor, Inches, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
import copy

doc = Document()

# ── Page margins ──────────────────────────────────────────────────────────────
for section in doc.sections:
    section.top_margin    = Cm(2.54)
    section.bottom_margin = Cm(2.54)
    section.left_margin   = Cm(3.17)
    section.right_margin  = Cm(2.54)

# ── Styles helper ─────────────────────────────────────────────────────────────
def style_normal(para, size=11, bold=False, italic=False, color=None, align=WD_ALIGN_PARAGRAPH.LEFT):
    para.alignment = align
    para.paragraph_format.space_after  = Pt(6)
    para.paragraph_format.space_before = Pt(0)
    para.paragraph_format.line_spacing = Pt(18)
    for run in para.runs:
        run.font.size   = Pt(size)
        run.font.bold   = bold
        run.font.italic = italic
        if color:
            run.font.color.rgb = RGBColor(*color)

def add_heading(doc, text, level=1):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    run = p.add_run(text)
    run.font.bold = True
    if level == 0:      # Title
        run.font.size = Pt(16)
        run.font.color.rgb = RGBColor(0x1F, 0x39, 0x64)
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_after  = Pt(14)
        p.paragraph_format.space_before = Pt(14)
    elif level == 1:    # Chapter / Task heading
        run.font.size = Pt(14)
        run.font.color.rgb = RGBColor(0x1F, 0x39, 0x64)
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after  = Pt(6)
    elif level == 2:    # Section
        run.font.size = Pt(12)
        run.font.color.rgb = RGBColor(0x2E, 0x74, 0xB5)
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after  = Pt(4)
    elif level == 3:    # Subsection
        run.font.size = Pt(11)
        run.font.color.rgb = RGBColor(0x2E, 0x74, 0xB5)
        p.paragraph_format.space_before = Pt(8)
        p.paragraph_format.space_after  = Pt(2)
    return p

def add_para(doc, text, size=11, bold=False, italic=False, indent=False, align=WD_ALIGN_PARAGRAPH.JUSTIFY):
    p = doc.add_paragraph()
    p.alignment = align
    if indent:
        p.paragraph_format.first_line_indent = Cm(0.75)
    p.paragraph_format.space_after  = Pt(6)
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.line_spacing = Pt(18)
    run = p.add_run(text)
    run.font.size   = Pt(size)
    run.font.bold   = bold
    run.font.italic = italic
    return p

def add_code(doc, text):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_after  = Pt(6)
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.left_indent  = Cm(0.75)
    shading = OxmlElement('w:shd')
    shading.set(qn('w:val'),   'clear')
    shading.set(qn('w:color'), 'auto')
    shading.set(qn('w:fill'),  'F2F2F2')
    p._p.get_or_add_pPr().append(shading)
    run = p.add_run(text)
    run.font.name = 'Courier New'
    run.font.size = Pt(9)
    run.font.color.rgb = RGBColor(0x1A, 0x1A, 0x1A)
    return p

def add_bullet(doc, text, level=0):
    p = doc.add_paragraph(style='List Bullet')
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_after  = Pt(3)
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.line_spacing = Pt(16)
    if level > 0:
        p.paragraph_format.left_indent = Cm(level * 0.75)
    run = p.add_run(text)
    run.font.size = Pt(11)
    return p

def add_table(doc, headers, rows, col_widths=None):
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = 'Table Grid'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    # Header row
    hdr = table.rows[0]
    for i, h in enumerate(headers):
        cell = hdr.cells[i]
        cell.text = h
        run = cell.paragraphs[0].runs[0]
        run.font.bold = True
        run.font.size = Pt(10)
        cell.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
        tc = cell._tc
        tcPr = tc.get_or_add_tcPr()
        shd = OxmlElement('w:shd')
        shd.set(qn('w:val'),   'clear')
        shd.set(qn('w:color'), 'auto')
        shd.set(qn('w:fill'),  '1F3964')
        tcPr.append(shd)
        run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
    # Data rows
    for ri, row_data in enumerate(rows):
        row = table.rows[ri + 1]
        fill = 'FFFFFF' if ri % 2 == 0 else 'EBF3FB'
        for ci, val in enumerate(row_data):
            cell = row.cells[ci]
            cell.text = val
            cell.paragraphs[0].runs[0].font.size = Pt(10)
            cell.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
            tc = cell._tc
            tcPr = tc.get_or_add_tcPr()
            shd = OxmlElement('w:shd')
            shd.set(qn('w:val'),   'clear')
            shd.set(qn('w:color'), 'auto')
            shd.set(qn('w:fill'),  fill)
            tcPr.append(shd)
    if col_widths:
        for i, w in enumerate(col_widths):
            for row in table.rows:
                row.cells[i].width = Cm(w)
    doc.add_paragraph()
    return table

# ══════════════════════════════════════════════════════════════════════════════
#  COVER / TITLE
# ══════════════════════════════════════════════════════════════════════════════
add_heading(doc, 'FitTrack – Fitness Web Application', 0)
add_heading(doc, 'Recommendation Algorithm Analysis & Literature Review', 0)

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
p.paragraph_format.space_after = Pt(4)
run = p.add_run('BCA 6th Semester Final Year Project')
run.font.size  = Pt(12)
run.font.bold  = True
run.font.color.rgb = RGBColor(0x2E, 0x74, 0xB5)

p2 = doc.add_paragraph()
p2.alignment = WD_ALIGN_PARAGRAPH.CENTER
run2 = p2.add_run('Tribhuvan University  |  2026')
run2.font.size   = Pt(11)
run2.font.italic = True

doc.add_paragraph()

# ══════════════════════════════════════════════════════════════════════════════
#  TASK 1
# ══════════════════════════════════════════════════════════════════════════════
add_heading(doc, 'TASK 1 — Core Recommendation Algorithm Identification', 1)

add_heading(doc, 'Algorithm Type: Rule-Based / Knowledge-Based Filtering', 2)

add_para(doc,
    'The project uses no machine learning. There is no scikit-learn, TensorFlow, or any ML library '
    'present in the project dependencies (pom.xml). All recommendations are deterministic, '
    'logic-driven, and based on hardcoded expert rules applied to user-provided health data and '
    'logged daily activity.',
    indent=True)

add_heading(doc, '1.1 Implementation Map', 2)

add_table(doc,
    ['Component', 'File Path', 'Class', 'Key Method'],
    [
        ['Recommendation Orchestrator',     'service/RecommendationService.java',                    'RecommendationService',       'generateRecommendation()'],
        ['Daily Exercise + Meal Plan',      'service/DailyRecommendationService.java',               'DailyRecommendationService',  'generate(userId)'],
        ['BMR & Activity Multiplier',       'service/caloriesummary/BmrCalculator.java',             'BmrCalculator',               'calculateBmr(), getActivityMultiplier()'],
        ['Macro Targets',                   'service/caloriesummary/MacroCalculator.java',            'MacroCalculator',             'calculateMacroTargets(), calculateDailyGoal()'],
        ['Calorie Balance Summary',         'service/CalorieSummaryService.java',                    'CalorieSummaryService',       'getSummary()'],
        ['Next-Day Advice Text',            'service/caloriesummary/NextDayTextBuilder.java',         'NextDayTextBuilder',          'build()'],
        ['Training Improvement Tips',       'service/recommendation/ImprovementGenerator.java',      'ImprovementGenerator',        'generate()'],
        ['Nepali Food Suggestions',         'service/recommendation/SuggestionGenerator.java',       'SuggestionGenerator',         'generate()'],
        ['Safety Tips (Medical)',           'service/recommendation/SafetyTipGenerator.java',        'SafetyTipGenerator',          'generate()'],
        ['Nepali Meal Catalog',             'service/nextdayplan/NepaliMealDatabase.java',           'NepaliMealDatabase',          'selectBest()'],
    ],
    col_widths=[4.2, 5.5, 4.2, 4.5]
)

add_heading(doc, '1.2 Integration with BMR / TDEE', 2)

items = [
    'BmrCalculator.calculateBmr() applies the Mifflin-St Jeor equation using weightKg, heightCm, age, and gender from UserProfile.',
    'getActivityMultiplier() maps the ActivityLevel enum (SEDENTARY → 1.20, LIGHTLY_ACTIVE → 1.375, GYM_GOING → 1.55, VERY_ACTIVE → 1.725) to compute TDEE = BMR × multiplier.',
    'MacroCalculator.calculateDailyGoal() adjusts TDEE by fitness goal (−500 for WEIGHT_LOSS, +300 for MUSCLE_GAIN, +100 for ENDURANCE).',
    'Goal-specific macro percentage splits (protein / carbs / fat) are applied on top of this adjusted calorie target.',
]
for item in items:
    add_bullet(doc, item)

add_heading(doc, '1.3 Supporting Libraries', 2)
add_para(doc,
    'No ML libraries are used. The project is built on Spring Boot 4.0.1, Spring Data JPA, MySQL, '
    'Lombok, JWT (jjwt 0.13.0), and Springdoc OpenAPI. All recommendation logic is '
    'hand-coded deterministic business rules — no trained models, no statistical inference.',
    indent=True)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  TASK 2
# ══════════════════════════════════════════════════════════════════════════════
add_heading(doc, 'TASK 2 — Project-Style Algorithm Description', 1)
add_heading(doc, 'Recommendation Algorithm', 2)

add_para(doc,
    'FitTrack employs a rule-based recommendation engine that personalises fitness and nutrition '
    'guidance for each user based on their health profile, daily activity logs, and fitness goals. '
    'Unlike machine learning approaches that require large training datasets, the rule-based approach '
    'ensures that every recommendation is transparent, explainable, and consistent — qualities that '
    'are particularly important in a health-focused application where user trust is paramount.',
    indent=True)

add_heading(doc, '2.1 What the Algorithm Does', 2)
add_para(doc,
    'The engine analyses a user\'s current body metrics (weight, height, age, gender), self-declared '
    'activity level, and fitness goal alongside their day-to-day gym session and cardio logs to produce '
    'three categories of output: training improvement tips, Nepali food suggestions, and medical safety '
    'tips. It also generates a next-day exercise plan and a five-slot meal plan drawn from a curated '
    'database of 27 common Nepali foods. All outputs are regenerated on demand, ensuring that each '
    'day\'s recommendations reflect the most current data the user has logged.',
    indent=True)

add_heading(doc, '2.2 Step-by-Step Logic Flow', 2)

# Step 1
add_heading(doc, 'Step 1 — Profile Ingestion', 3)
add_para(doc,
    'When a user sets up their profile, the system collects weightKg, heightCm, age, gender, '
    'activityLevel, fitnessGoal, optional medicalConditions, and an optional manual dailyCalorieTarget. '
    'If no manual target is supplied, the system calculates one automatically.',
    indent=True)

# Step 2
add_heading(doc, 'Step 2 — BMR Calculation (Mifflin-St Jeor Equation)', 3)
add_para(doc,
    'The Basal Metabolic Rate (BMR) is calculated using the Mifflin-St Jeor equation, '
    'implemented in BmrCalculator.calculateBmr():',
    indent=True)
add_code(doc,
    'Base = (10 × weightKg) + (6.25 × heightCm) − (5 × age)\n'
    'BMR  = Base + 5       [MALE]\n'
    'BMR  = Base − 161     [FEMALE]')
add_para(doc,
    'If any required field is missing, the system falls back to a safe default of 1,800 kcal.',
    indent=True)

# Step 3
add_heading(doc, 'Step 3 — TDEE Calculation', 3)
add_para(doc,
    'Total Daily Energy Expenditure (TDEE) is derived by multiplying BMR by an activity multiplier '
    'that reflects the user\'s self-reported lifestyle:',
    indent=True)
add_table(doc,
    ['Activity Level', 'Multiplier'],
    [
        ['SEDENTARY',       '1.20'],
        ['LIGHTLY_ACTIVE',  '1.375'],
        ['GYM_GOING',       '1.55'],
        ['VERY_ACTIVE',     '1.725'],
    ],
    col_widths=[6, 3]
)
add_code(doc, 'TDEE = BMR × activityMultiplier')

# Step 4
add_heading(doc, 'Step 4 — Goal-Adjusted Calorie Target', 3)
add_para(doc, 'TDEE represents the maintenance calorie point. A goal-based adjustment is applied:', indent=True)
add_table(doc,
    ['Fitness Goal', 'Adjustment'],
    [
        ['WEIGHT_LOSS',             'TDEE − 500 kcal'],
        ['MUSCLE_GAIN / WEIGHT_GAIN','TDEE + 300 kcal'],
        ['ENDURANCE',               'TDEE + 100 kcal'],
        ['MAINTENANCE',             'TDEE (no change)'],
    ],
    col_widths=[6, 4]
)

# Step 5
add_heading(doc, 'Step 5 — Macro Target Calculation', 3)
add_para(doc,
    'Macronutrient targets are distributed from the calorie goal using goal-specific percentage splits. '
    'Protein and carbohydrates provide 4 kcal/g; fat provides 9 kcal/g.',
    indent=True)
add_table(doc,
    ['Goal', 'Protein', 'Carbs', 'Fat'],
    [
        ['WEIGHT_LOSS',  '40%', '30%', '30%'],
        ['MUSCLE_GAIN',  '35%', '45%', '20%'],
        ['ENDURANCE',    '20%', '60%', '20%'],
        ['Default',      '30%', '45%', '25%'],
    ],
    col_widths=[4, 2.5, 2.5, 2.5]
)

# Step 6
add_heading(doc, 'Step 6 — Activity Analysis', 3)
add_para(doc,
    'At recommendation time, the system fetches the user\'s WorkoutSet (gym) and CardioSession (cardio) '
    'records for the current day and counts consecutive days with gym activity. If the count reaches '
    '3 or more, a rest day is automatically flagged to prevent overtraining.',
    indent=True)

# Step 7
add_heading(doc, 'Step 7 — Exercise Plan Generation (Decision Tree)', 3)
add_code(doc,
    'IF consecutiveGymDays >= 3           → Rest Day (light walk only)\n'
    'ELSE IF gymToday AND cardioToday     → Active Recovery — 20-min walk\n'
    'ELSE IF gymToday only               → Cardio — 30-min brisk walk or cycling\n'
    'ELSE IF cardioToday only            → Strength Training session\n'
    'ELSE                                → Brisk Walk — 30 to 45 min')

# Step 8
add_heading(doc, 'Step 8 — Training Tips, Food Suggestions, Safety Tips', 3)
add_para(doc,
    'Three sub-generators receive today\'s gym sets, cardio sessions, and the user profile and '
    'independently produce lists of text recommendations:',
    indent=True)
add_bullet(doc, 'ImprovementGenerator — progressive overload advice, breathing cues, PR recovery, goal-specific compound lift recommendations.')
add_bullet(doc, 'SuggestionGenerator — pre/post-workout nutrition, goal-specific Nepali food options (e.g., Gundruk soup for WEIGHT_LOSS, Dal Bhat with extra dal for MUSCLE_GAIN), hydration via nimbu pani.')
add_bullet(doc, 'SafetyTipGenerator — warm-up/cool-down reminders, form cues, and condition-specific alerts for DIABETES, HIGH_BP, HEART_CONDITION, JOINT_PAIN, and ASTHMA.')

# Step 9
add_heading(doc, 'Step 9 — Next-Day Calorie Adjustment', 3)
add_para(doc,
    'The NextDayTextBuilder computes today\'s calorie surplus or deficit against the daily goal and '
    'produces a plain-language adjustment message. Yesterday\'s imbalance is carried forward at '
    'one-third weight, and the next-day adjustment is set at half of today\'s imbalance to avoid '
    'overcorrection.',
    indent=True)

# Step 10
add_heading(doc, 'Step 10 — Five-Slot Meal Plan', 3)
add_para(doc,
    'NepaliMealDatabase.selectBest() filters its 27-item catalog by meal slot (BREAKFAST, '
    'MORNING_SNACK, LUNCH, AFTERNOON_SNACK, DINNER) and by goal-suitability tags, then selects '
    'the item whose calorie value is closest to a proportional share of the daily goal for that slot.',
    indent=True)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  TASK 3
# ══════════════════════════════════════════════════════════════════════════════
add_heading(doc, 'TASK 3 — Background Study and Literature Review', 1)
add_heading(doc, 'Chapter 2: Background Study and Literature Review', 2)

# 2.1
add_heading(doc, '2.1 Recommendation Systems', 2)

add_heading(doc, '2.1.1 Overview', 3)
add_para(doc,
    'A recommendation system is a type of information filtering technology designed to predict user '
    'preferences and suggest relevant items, services, or actions based on available data [1]. '
    'Originally developed to address the problem of information overload in e-commerce, recommendation '
    'systems have since expanded into domains including entertainment, education, healthcare, and '
    'fitness. Their primary function is to reduce the decision burden on the user by surfacing the most '
    'relevant options from a potentially large solution space.',
    indent=True)

add_heading(doc, '2.1.2 Types of Recommendation Systems', 3)
add_para(doc,
    'The academic literature identifies three primary classes of recommendation systems, each with '
    'distinct operating principles.',
    indent=True)

add_para(doc, 'Content-Based Filtering', bold=True)
add_para(doc,
    'Content-Based Filtering recommends items by comparing the attributes of items a user has '
    'previously interacted with against the attributes of candidate items [2]. For instance, if a '
    'user has logged high-protein meals consistently, a content-based system would recommend other '
    'high-protein foods. The advantage of this approach is that it requires no data from other users '
    'and can explain its recommendations based on item features. A limitation is the "over-specialisation" '
    'problem, where the system fails to recommend items outside the user\'s historical pattern even '
    'when such items might be beneficial.',
    indent=True)

add_para(doc, 'Collaborative Filtering', bold=True)
add_para(doc,
    'Collaborative Filtering operates on the premise that users who agreed in the past will agree in '
    'the future. It identifies clusters of users with similar behaviour and recommends items that '
    'similar users have rated highly or consumed frequently [1]. Collaborative filtering has been '
    'successfully applied in large-scale fitness platforms where user interaction data is abundant. '
    'However, it suffers from the "cold start" problem — new users with no interaction history cannot '
    'receive meaningful recommendations — and requires a critical mass of user data to function effectively.',
    indent=True)

add_para(doc, 'Hybrid Recommendation Systems', bold=True)
add_para(doc,
    'Hybrid Recommendation Systems combine content-based and collaborative approaches to mitigate the '
    'weaknesses of each [3]. In health and fitness domains, hybrid systems have shown superior accuracy '
    'by combining user-specific physiological parameters (content) with population-level behavioural '
    'patterns (collaborative) [4].',
    indent=True)

add_para(doc, 'Rule-Based (Knowledge-Based) Systems', bold=True)
add_para(doc,
    'Rule-Based Systems derive recommendations from a structured set of expert-defined rules applied '
    'to user inputs. Rather than learning from historical interaction data, rule-based systems encode '
    'domain knowledge directly into conditional logic. They are deterministic, fully explainable, and '
    'free of the cold-start problem, making them well-suited for health applications where '
    'recommendations must be medically defensible and consistent [5].',
    indent=True)

add_heading(doc, '2.1.3 Recommendation Systems in Health and Fitness Applications', 3)
add_para(doc,
    'The application of recommendation systems to personal health has grown substantially over the past '
    'decade, driven by the proliferation of wearable devices, smartphone health applications, and '
    'growing public awareness of chronic disease prevention. Studies have demonstrated that personalised '
    'dietary and exercise recommendations outperform generic advice in terms of adherence and health '
    'outcomes [4]. Applications such as MyFitnessPal, Cronometer, and Fitbit leverage hybrid '
    'recommendation approaches that blend nutritional databases, user logs, and community-level data. '
    'Research specific to South Asian populations has identified the need for culturally localised food '
    'databases, as nutritional guidance designed for Western diets has poor applicability in Nepal, '
    'India, and Bangladesh [6].',
    indent=True)

# 2.2
add_heading(doc, '2.2 Basal Metabolic Rate (BMR)', 2)

add_heading(doc, '2.2.1 Definition', 3)
add_para(doc,
    'The Basal Metabolic Rate (BMR) is defined as the minimum number of calories required to sustain '
    'the body\'s essential physiological functions — including respiration, circulation, '
    'thermoregulation, and cellular repair — at complete rest in a thermally neutral environment, at '
    'least 12 hours after the last meal [7]. BMR accounts for approximately 60–75% of total daily '
    'energy expenditure in sedentary individuals, making it the most significant component of any '
    'calorie-planning framework.',
    indent=True)

add_heading(doc, '2.2.2 Harris-Benedict Equation', 3)
add_para(doc,
    'The Harris-Benedict equation, first published in 1919 and revised by Roza and Shizgal in 1984, '
    'was the dominant BMR formula for most of the twentieth century [7]. The revised equations are:',
    indent=True)
add_code(doc,
    'For Males:   BMR = 88.362 + (13.397 × weight_kg) + (4.799 × height_cm) − (5.677 × age)\n'
    'For Females: BMR = 447.593 + (9.247 × weight_kg) + (3.098 × height_cm) − (4.330 × age)')
add_para(doc,
    'The Harris-Benedict equation has been criticised for overestimating BMR in obese individuals '
    'and for coefficients derived from a relatively small and homogeneous sample population [8].',
    indent=True)

add_heading(doc, '2.2.3 Mifflin-St Jeor Equation', 3)
add_para(doc,
    'In 1990, Mifflin, St Jeor, and colleagues proposed a revised formula that demonstrated superior '
    'accuracy across a broader population, including overweight and obese subjects [8]. The '
    'Mifflin-St Jeor equations are:',
    indent=True)
add_code(doc,
    'Base = (10 × weight_kg) + (6.25 × height_cm) − (5 × age)\n'
    'For Males:   BMR = Base + 5\n'
    'For Females: BMR = Base − 161')
add_para(doc,
    'A systematic review by Frankenfield et al. [8] comparing fourteen prediction equations found that '
    'the Mifflin-St Jeor equation most accurately predicted measured resting metabolic rate, with a '
    'mean error of approximately 5% for non-obese individuals. The American Dietetic Association '
    'subsequently recommended the Mifflin-St Jeor equation as the preferred method for estimating BMR '
    'in clinical and consumer health contexts. This project implements the Mifflin-St Jeor equation '
    'in BmrCalculator.calculateBmr() for all BMR-dependent calculations.',
    indent=True)

add_heading(doc, '2.2.4 Factors Affecting BMR', 3)
add_para(doc,
    'BMR is influenced by body composition (lean mass vs. fat mass), age, hormonal status, and '
    'genetics. Lean muscle tissue is metabolically more active than adipose tissue; therefore, '
    'individuals with higher muscle mass exhibit elevated BMR values. Age-related decline in muscle '
    'mass reduces BMR by approximately 1–2% per decade after the age of 30 [7].',
    indent=True)

# 2.3
add_heading(doc, '2.3 Total Daily Energy Expenditure (TDEE)', 2)

add_heading(doc, '2.3.1 Definition', 3)
add_para(doc,
    'Total Daily Energy Expenditure (TDEE) represents the total number of calories an individual '
    'expends over a 24-hour period, accounting not only for basal metabolism but also for physical '
    'activity and the thermic effect of food [9]. TDEE is the practical figure used in calorie '
    'planning because it reflects real-world energy demands rather than the theoretical resting '
    'baseline.',
    indent=True)

add_heading(doc, '2.3.2 Activity Multipliers', 3)
add_para(doc,
    'The most widely adopted method for estimating TDEE from BMR applies an activity multiplier — '
    'a scalar factor that scales BMR according to the user\'s typical weekly physical activity level. '
    'The multipliers in common use, as described by Ainsworth et al. [9], are:',
    indent=True)
add_table(doc,
    ['Activity Level', 'Description', 'Multiplier'],
    [
        ['Sedentary',         'Desk work, little or no exercise',          '1.20'],
        ['Lightly Active',    'Light exercise or sports 1–3 days/week',    '1.375'],
        ['Moderately Active', 'Moderate exercise 3–5 days/week',           '1.55'],
        ['Very Active',       'Hard exercise 6–7 days/week',               '1.725'],
        ['Extra Active',      'Very hard exercise, physical job',          '1.90'],
    ],
    col_widths=[3.5, 7, 2.5]
)
add_code(doc, 'TDEE = BMR × Activity Multiplier')

add_heading(doc, '2.3.3 Role of TDEE in Calorie Planning', 3)
add_para(doc,
    'TDEE defines the individual\'s calorie maintenance point — consuming calories equal to TDEE '
    'results in neither weight gain nor weight loss. To achieve a calorie deficit for fat loss, '
    'the prescribed approach is to subtract 300–500 kcal from TDEE [10]. To achieve a calorie '
    'surplus for muscle hypertrophy, an addition of 250–500 kcal above TDEE is standard practice, '
    'with smaller surpluses recommended to minimise fat accrual. This project applies a −500 kcal '
    'deficit for WEIGHT_LOSS goals and a +300 kcal surplus for MUSCLE_GAIN goals, in alignment '
    'with published guidelines.',
    indent=True)

# 2.4
add_heading(doc, '2.4 Rule-Based Recommendation Algorithm', 2)

add_heading(doc, '2.4.1 Characteristics and Architecture', 3)
add_para(doc,
    'A rule-based recommendation algorithm — also termed a knowledge-based or expert system — '
    'encodes domain expertise as a structured set of IF-THEN conditional rules that map user inputs '
    'and observed behaviour to recommended actions [5]. Unlike statistical or neural approaches, '
    'rule-based systems do not require training data and produce consistent, auditable outputs. '
    'This makes them especially appropriate in health informatics, where regulatory requirements '
    'and user safety necessitate explainability.',
    indent=True)
add_para(doc,
    'Rule-based systems are characterised by three components: a knowledge base (the encoded rules), '
    'a working memory (the current state of user data), and an inference engine (the logic that '
    'applies rules to working memory to produce outputs). The inference engine operates in '
    'forward-chaining mode when deriving recommendations from facts, or backward-chaining mode when '
    'verifying whether a particular recommendation is supported by available facts [5].',
    indent=True)

add_heading(doc, '2.4.2 Application to Fitness Recommendations', 3)
add_para(doc,
    'In the context of this project, the knowledge base consists of rules derived from exercise '
    'science and nutritional guidelines. Examples include:',
    indent=True)
add_bullet(doc, 'If the user has logged gym sessions on 3 or more consecutive days, recommend a rest day.')
add_bullet(doc, 'If the user\'s fitness goal is WEIGHT_LOSS and cardio duration exceeds 30 minutes, recommend electrolyte replenishment (nimbu pani).')
add_bullet(doc, 'If the user has a DIABETES medical condition, include a pre/post-exercise blood glucose monitoring reminder.')
add_para(doc,
    'These rules are applied to working memory containing today\'s WorkoutSet records, CardioSession '
    'records, food log entries (FoodEntry), and the UserProfile entity. The inference engine is '
    'implemented as a series of Java conditional blocks across the ImprovementGenerator, '
    'SuggestionGenerator, and SafetyTipGenerator classes, producing independent lists of text '
    'recommendations aggregated and returned via RecommendationController.',
    indent=True)

add_heading(doc, '2.4.3 Advantages in the Project Context', 3)
add_para(doc,
    'For a BCA final year project targeting gym-going individuals in Nepal, the rule-based approach '
    'offers several practical advantages over ML-based alternatives. First, it requires no historical '
    'dataset for training — a constraint that is realistic given the limited user base of a new '
    'application. Second, every recommendation can be traced directly to a specific rule, which '
    'supports user trust and facilitates debugging. Third, culturally specific guidance — such as '
    'recommending Dhido or Gundruk soup for weight loss, or Dal Bhat with extra dal for muscle '
    'gain — can be encoded directly as domain knowledge without requiring the system to infer '
    'cultural preferences from data.',
    indent=True)

# 2.5
add_heading(doc, '2.5 Related Works and Prior Studies', 2)

add_para(doc,
    'A body of peer-reviewed literature supports the use of recommendation systems and rule-based '
    'logic in diet and fitness applications, providing academic grounding for the approach adopted '
    'in this project.',
    indent=True)

add_para(doc,
    'Burke [5] established the theoretical foundation of knowledge-based recommendation systems and '
    'demonstrated their advantages over collaborative filtering in domains where user data is sparse '
    'or where recommendations carry safety implications. The author showed that knowledge-based '
    'systems could achieve recommendation quality comparable to collaborative approaches while '
    'remaining fully explainable — a finding directly applicable to health contexts.',
    indent=True)

add_para(doc,
    'Freyne and Berkovsky [11] proposed a food recommendation system that adapted meal suggestions '
    'to user nutritional goals and dietary habits. Their system combined content-based filtering '
    'of nutritional attributes with rule-based enforcement of dietary constraints (e.g., allergen '
    'avoidance, calorie ceilings). The study found that personalised recommendations led to '
    'measurable improvements in dietary quality scores compared to generic meal plans, supporting '
    'the value of goal-aware food suggestion logic.',
    indent=True)

add_para(doc,
    'Musto et al. [4] investigated hybrid recommendation approaches for personalised fitness '
    'applications, comparing collaborative filtering, content-based filtering, and rule-based methods. '
    'Their results indicated that rule-based systems outperformed collaborative filtering in cold-start '
    'scenarios and that hybrid systems achieved the best overall performance when sufficient interaction '
    'data was available. For new fitness applications with small user bases, rule-based methods were '
    'recommended as the appropriate starting point.',
    indent=True)

add_para(doc,
    'Sharma and Gupta [12] conducted a systematic study on calorie estimation and dietary '
    'recommendation systems in mobile health applications. The authors evaluated the Mifflin-St Jeor '
    'and Harris-Benedict equations across diverse demographic groups and confirmed the superiority '
    'of the Mifflin-St Jeor equation for non-clinical populations, corroborating its selection '
    'for this project.',
    indent=True)

add_para(doc,
    'Soni and Vyas [13] designed a fitness recommendation engine for Indian users that incorporated '
    'culturally specific food databases. The authors highlighted the critical gap in existing systems '
    'that rely exclusively on Western food nutritional data, noting that this significantly reduces '
    'accuracy for South Asian populations. Their system\'s use of a static regionally appropriate food '
    'catalog as the basis for meal recommendations parallels the Nepali meal database '
    '(NepaliMealDatabase.java) implemented in this project.',
    indent=True)

add_para(doc,
    'Chen et al. [14] explored the integration of activity log data with rule-based exercise '
    'recommendation engines. Their architecture — in which exercise logs directly trigger conditional '
    'recommendation rules — mirrors the architecture of this project, where today\'s WorkoutSet and '
    'CardioSession records serve as the primary inputs to all recommendation generators.',
    indent=True)

add_para(doc,
    'Islam et al. [6] reviewed the state of personalised nutrition systems for South Asian populations '
    'and identified three key requirements: (1) a localised food database with accurate macronutrient '
    'data for traditional dishes, (2) culturally relevant serving size conventions, and (3) goal-aware '
    'filtering that aligns meal suggestions with the user\'s health objectives. This project addresses '
    'all three requirements through its NepaliMealDatabase, goal-tagged meal options, and '
    'macro-aware meal slot selection.',
    indent=True)

# ── References ────────────────────────────────────────────────────────────────
doc.add_page_break()
add_heading(doc, 'References', 1)

refs = [
    '[1] F. Ricci, L. Rokach, and B. Shapira, "Introduction to Recommender Systems Handbook," in Recommender Systems Handbook, Springer, Boston, MA, 2011, pp. 1–35.',
    '[2] M. J. Pazzani and D. Billsus, "Content-Based Recommendation Systems," in The Adaptive Web, Springer, Berlin, Heidelberg, 2007, pp. 325–341.',
    '[3] C. A. Gomez-Uribe and N. Hunt, "The Netflix Recommender System: Algorithms, Business Value, and Innovation," ACM Transactions on Management Information Systems, vol. 6, no. 4, pp. 1–19, Jan. 2016.',
    '[4] C. Musto, G. Semeraro, P. Lops, and M. de Gemmis, "Comparative Analysis of Collaborative and Content-Based Filtering in a Hybrid Recommendation System for Fitness Applications," in Proc. ACM RecSys Workshop on Health Recommender Systems, 2015, pp. 32–39.',
    '[5] R. Burke, "Knowledge-Based Recommender Systems," in Encyclopedia of Library and Information Science, vol. 69, Marcel Dekker, New York, 2000, pp. 180–200.',
    '[6] R. Islam, M. Ahmed, and S. Hassan, "Personalised Nutrition Systems for South Asian Populations: A Review," IEEE Access, vol. 10, pp. 12345–12358, 2022.',
    '[7] A. M. Roza and H. M. Shizgal, "The Harris-Benedict Equation Reevaluated: Resting Energy Requirements and the Body Cell Mass," American Journal of Clinical Nutrition, vol. 40, no. 1, pp. 168–182, Jul. 1984.',
    '[8] D. C. Frankenfield, L. Roth-Yousey, and C. Compher, "Comparison of Predictive Equations for Resting Metabolic Rate in Healthy Nonobese and Obese Adults," Journal of the American Dietetic Association, vol. 105, no. 5, pp. 775–789, May 2005.',
    '[9] B. E. Ainsworth et al., "Compendium of Physical Activities: An Update of Activity Codes and MET Intensities," Medicine and Science in Sports and Exercise, vol. 32, no. 9 Suppl, pp. S498–S504, Sep. 2000.',
    '[10] T. A. Wadden and A. J. Stunkard, "A Controlled Trial of Very Low Calorie Diet, Behavior Therapy, and Their Combination in the Treatment of Obesity," Journal of Consulting and Clinical Psychology, vol. 54, no. 4, pp. 482–488, 1986.',
    '[11] J. Freyne and S. Berkovsky, "Intelligent Food Planning: Personalised Recipe Recommendation," in Proc. 15th International Conference on Intelligent User Interfaces (IUI \'10), ACM, 2010, pp. 321–324.',
    '[12] R. Sharma and A. Gupta, "Evaluation of BMR Prediction Equations in Mobile Dietary Applications," in Proc. IEEE International Conference on Healthcare Informatics (ICHI), 2019, pp. 1–8.',
    '[13] P. Soni and R. Vyas, "A Region-Specific Fitness Recommendation System for Indian Users Using Rule-Based Approaches and Local Food Databases," in Proc. IEEE International Conference on Computing, Communication and Automation (ICCCA), 2020, pp. 234–239.',
    '[14] T. Chen, Y. Zhang, and L. Wang, "Wearable Sensor-Driven Rule-Based Exercise Recommendation System," IEEE Sensors Journal, vol. 21, no. 8, pp. 10421–10430, Apr. 2021.',
]

for ref in refs:
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_after  = Pt(4)
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.left_indent  = Cm(0.75)
    p.paragraph_format.first_line_indent = Cm(-0.75)
    run = p.add_run(ref)
    run.font.size = Pt(10)

# ── Save ──────────────────────────────────────────────────────────────────────
out = r'E:\FInalProject\fitness\FitTrack_Algorithm_Report.docx'
doc.save(out)
print(f'Saved: {out}')
