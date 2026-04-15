from docx import Document
from docx.shared import Pt, RGBColor, Inches, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
import copy

doc = Document()

# ── Page margins ──────────────────────────────────────────────────────────────
section = doc.sections[0]
section.top_margin    = Cm(2.0)
section.bottom_margin = Cm(2.0)
section.left_margin   = Cm(2.5)
section.right_margin  = Cm(2.5)

# ── Helpers ───────────────────────────────────────────────────────────────────
def set_font(run, name="Calibri", size=11, bold=False, color=None):
    run.font.name  = name
    run.font.size  = Pt(size)
    run.font.bold  = bold
    if color:
        run.font.color.rgb = RGBColor(*color)

def heading1(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(16)
    p.paragraph_format.space_after  = Pt(4)
    run = p.add_run(text)
    set_font(run, size=16, bold=True, color=(31, 73, 125))
    return p

def heading2(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(12)
    p.paragraph_format.space_after  = Pt(2)
    run = p.add_run(text)
    set_font(run, size=13, bold=True, color=(0, 70, 127))
    return p

def heading3(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after  = Pt(2)
    run = p.add_run(text)
    set_font(run, size=11, bold=True, color=(55, 55, 55))
    return p

def body(text, indent=False):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(4)
    if indent:
        p.paragraph_format.left_indent = Cm(0.6)
    run = p.add_run(text)
    set_font(run, size=10.5)
    return p

def bullet(text, level=0):
    p = doc.add_paragraph(style="List Bullet")
    p.paragraph_format.left_indent  = Cm(0.6 + level * 0.5)
    p.paragraph_format.space_after  = Pt(2)
    run = p.add_run(text)
    set_font(run, size=10.5)
    return p

def add_table(headers, rows, col_widths=None):
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = "Table Grid"
    table.alignment = WD_TABLE_ALIGNMENT.LEFT
    # Header row
    hrow = table.rows[0]
    for i, h in enumerate(headers):
        cell = hrow.cells[i]
        cell.paragraphs[0].clear()
        run = cell.paragraphs[0].add_run(h)
        set_font(run, size=10, bold=True, color=(255, 255, 255))
        # Dark blue fill
        tc   = cell._tc
        tcPr = tc.get_or_add_tcPr()
        shd  = OxmlElement("w:shd")
        shd.set(qn("w:val"), "clear")
        shd.set(qn("w:color"), "auto")
        shd.set(qn("w:fill"), "1F497D")
        tcPr.append(shd)
    # Data rows
    for ri, row_data in enumerate(rows):
        row = table.rows[ri + 1]
        fill = "EEF3F9" if ri % 2 == 0 else "FFFFFF"
        for ci, cell_text in enumerate(row_data):
            cell = row.cells[ci]
            cell.paragraphs[0].clear()
            run = cell.paragraphs[0].add_run(str(cell_text))
            set_font(run, size=10)
            tc   = cell._tc
            tcPr = tc.get_or_add_tcPr()
            shd  = OxmlElement("w:shd")
            shd.set(qn("w:val"), "clear")
            shd.set(qn("w:color"), "auto")
            shd.set(qn("w:fill"), fill)
            tcPr.append(shd)
    if col_widths:
        for i, w in enumerate(col_widths):
            for row in table.rows:
                row.cells[i].width = Cm(w)
    doc.add_paragraph()
    return table

def divider():
    p = doc.add_paragraph()
    pPr = p._p.get_or_add_pPr()
    pBdr = OxmlElement("w:pBdr")
    bottom = OxmlElement("w:bottom")
    bottom.set(qn("w:val"), "single")
    bottom.set(qn("w:sz"), "6")
    bottom.set(qn("w:space"), "1")
    bottom.set(qn("w:color"), "1F497D")
    pBdr.append(bottom)
    pPr.append(pBdr)
    return p

# ══════════════════════════════════════════════════════════════════════════════
#  TITLE PAGE
# ══════════════════════════════════════════════════════════════════════════════
title_p = doc.add_paragraph()
title_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
title_p.paragraph_format.space_before = Pt(40)
r = title_p.add_run("FitTrack")
set_font(r, size=36, bold=True, color=(31, 73, 125))

sub_p = doc.add_paragraph()
sub_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
r2 = sub_p.add_run("Fitness & Wellness Tracking Platform")
set_font(r2, size=18, color=(0, 112, 192))

doc.add_paragraph()
info_p = doc.add_paragraph()
info_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
r3 = info_p.add_run("Project Technical Documentation")
set_font(r3, size=13, bold=True, color=(64, 64, 64))

doc.add_paragraph()
meta_p = doc.add_paragraph()
meta_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
r4 = meta_p.add_run("Author: Sarthak Lama     |     Date: April 2026     |     Version: 1.0")
set_font(r4, size=11, color=(100, 100, 100))

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  1. PROJECT OVERVIEW
# ══════════════════════════════════════════════════════════════════════════════
heading1("1.  Project Overview")
divider()
body(
    "FitTrack is a full-stack fitness and wellness tracking platform built for users who want to "
    "monitor their daily nutrition, gym workouts, cardio sessions, body measurements, and weight "
    "progress — all in one place. The platform provides a personalised daily calorie summary, "
    "week-over-week progress charts, and an AI-style recommendation engine that generates a "
    "Today's Analysis report and a Tomorrow's Plan based on real logged data."
)
body(
    "The application is built using a Spring Boot REST backend with a MySQL database and a "
    "plain HTML/CSS/JavaScript frontend — no heavy frontend framework is used, making it "
    "lightweight and easy to deploy."
)

heading2("1.1  Key Capabilities")
bullets_overview = [
    "User registration and JWT-secured login",
    "Nutritional food logging with macro tracking (calories, protein, carbs, fat)",
    "Gym workout tracking: exercises, sets, reps, weight, muscle group, and estimated calories burned",
    "Cardio session tracking: activity type, duration, distance, and calories burned",
    "Body weight and measurement logging over time",
    "Daily calorie summary with BMR/TDEE calculation, deficit/surplus status, and next-day advice",
    "Weekly progress report with per-day aggregates and 7-day summaries",
    "Daily recommendation engine: Today's Analysis + Tomorrow's personalised exercise & meal plan",
    "User profile management: weight, height, age, gender, fitness goal, activity level, calorie/protein targets",
    "Responsive single-page-style frontend with sidebar navigation",
]
for b in bullets_overview:
    bullet(b)

heading2("1.2  Technology Stack")
add_table(
    ["Layer", "Technology", "Version / Notes"],
    [
        ["Backend Framework",  "Spring Boot",       "3.x — REST API, auto-configuration"],
        ["Language",           "Java",              "21 (LTS)"],
        ["Security",           "Spring Security",   "JWT stateless auth, BCrypt password hashing"],
        ["ORM / Persistence",  "Spring Data JPA",   "Hibernate — DDL auto-update"],
        ["Database",           "MySQL",             "8.x — schema: fitness_db"],
        ["Build Tool",         "Maven",             "Wrapper included (.mvnw)"],
        ["API Documentation",  "Springdoc OpenAPI", "Swagger UI at /swagger-ui.html"],
        ["Frontend",           "HTML5 + CSS3 + JS", "Vanilla — no framework"],
        ["HTTP Client (FE)",   "Fetch API",         "api.js wrapper, Bearer token + X-User-ID header"],
        ["Lombok",             "Lombok",            "Reduces boilerplate — @Data, @Builder, @RequiredArgsConstructor"],
    ],
    col_widths=[4.5, 4.5, 7.5]
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  2. ARCHITECTURE
# ══════════════════════════════════════════════════════════════════════════════
heading1("2.  System Architecture")
divider()
body(
    "FitTrack follows the standard Spring Boot layered architecture: "
    "Controller → Service → Repository → Database. "
    "The frontend communicates with the backend exclusively through a JSON REST API over HTTP. "
    "Authentication is stateless — every request carries a JWT Bearer token and an X-User-ID header."
)

heading2("2.1  Package Structure")
add_table(
    ["Package", "Contents"],
    [
        ["com.project.fitness",                       "FitnessApplication — Spring Boot entry point"],
        ["…controller",                               "9 REST controllers (one per domain)"],
        ["…service",                                  "13 service classes + 4 sub-packages"],
        ["…service.caloriesummary",                   "BmrCalculator, MacroCalculator, NextDayTextBuilder"],
        ["…service.nextdayplan",                      "ExercisePlanBuilder, MealPlanBuilder, NepaliMealDatabase, ReminderBuilder"],
        ["…service.recommendation",                   "ImprovementGenerator, SafetyTipGenerator, SuggestionGenerator"],
        ["…service.weeklyprogress",                   "DailyAggregator, WeekData, WeeklyDataFetcher, WeeklySummaryBuilder"],
        ["…model",                                    "12 JPA entities + 8 enums"],
        ["…repository",                               "9 Spring Data JPA repositories"],
        ["…dto",                                      "28 request/response DTOs"],
        ["…security",                                 "SecurityConfig, JwtUtils, JwtAuthenticationFilter, CustomUserDetailsService"],
        ["…config",                                   "OpenAPIConfig — Swagger/OpenAPI 3 setup"],
        ["…exceptions",                               "GlobalExceptionHandler — centralised error responses"],
    ],
    col_widths=[6.5, 10.0]
)

heading2("2.2  Security Architecture")
body(
    "Spring Security is configured as a fully stateless filter chain. "
    "Every non-public endpoint requires a valid JWT. "
    "The JwtAuthenticationFilter extracts the token from the Authorization: Bearer … header, "
    "validates it, and sets the SecurityContext so downstream controllers receive an authenticated principal. "
    "Passwords are hashed with BCrypt before storage."
)
add_table(
    ["Endpoint Pattern", "Access"],
    [
        ["POST /api/auth/register, POST /api/auth/login", "Public — no token required"],
        ["/swagger-ui/**, /v3/api-docs/**",               "Public — API documentation"],
        ["/*.html, /css/**, /js/**",                      "Public — static frontend files"],
        ["/api/admin/**",                                 "ADMIN role only"],
        ["All other /api/** endpoints",                   "Authenticated (valid JWT)"],
    ],
    col_widths=[9.0, 7.5]
)

heading2("2.3  JWT Details")
bullet("Algorithm: HMAC-SHA256 (HS256)")
bullet("Payload claims: sub (userId), roles list, iat, exp")
bullet("Token expiry: 48 hours (172 800 000 ms)")
bullet("Frontend stores token + userId in localStorage; all requests attach Authorization: Bearer <token> and X-User-ID: <id>")

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  3. DATA MODEL
# ══════════════════════════════════════════════════════════════════════════════
heading1("3.  Data Model")
divider()
body("All entities are JPA-managed and map to MySQL tables. Hibernate DDL is set to auto-update.")

heading2("3.1  Entities")
add_table(
    ["Entity", "Table", "Key Fields"],
    [
        ["User",             "user",             "id (UUID), username, email, passwordHash, role (USER/ADMIN)"],
        ["UserProfile",      "user_profile",     "userId (1:1), weightKg, heightCm, age, gender, fitnessGoal, activityLevel, dailyCalorieTarget, dailyProteinTarget, medicalConditions (JSON)"],
        ["FoodEntry",        "food_entry",       "userId, foodName, calories, protein, carbs, fat, servingSize, mealType, loggedAt"],
        ["WorkoutSet",       "workout_set",      "userId, exerciseName, muscleGroup, sets, reps, weightKg, caloriesBurned, isPR, loggedAt"],
        ["CardioSession",    "cardio_session",   "userId, activityType, cardioType, durationMinutes, caloriesBurned, distanceKm, loggedAt"],
        ["WeightLog",        "weight_log",       "userId, weightKg, loggedAt"],
        ["BodyMeasurement",  "body_measurement", "userId, chestCm, waistCm, hipsCm, armsCm, thighsCm, loggedAt"],
        ["Activity",         "activity",         "userId, activityType, duration, caloriesBurned, notes, createdAt"],
        ["Recommendation",   "recommendation",   "userId, activityId, suggestions (list), improvements (list), safetyTips (list)"],
    ],
    col_widths=[3.5, 4.0, 9.0]
)

heading2("3.2  Enums")
add_table(
    ["Enum", "Values"],
    [
        ["FitnessGoal",   "WEIGHT_LOSS, WEIGHT_GAIN, MUSCLE_GAIN, MAINTENANCE, ENDURANCE, FLEXIBILITY"],
        ["ActivityLevel", "SEDENTARY, LIGHTLY_ACTIVE, GYM_GOING, VERY_ACTIVE"],
        ["MuscleGroup",   "CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE, FULL_BODY, GLUTES, CARDIO"],
        ["MealType",      "BREAKFAST, LUNCH, DINNER, SNACK"],
        ["ActivityType",  "RUNNING, WALKING, CYCLING, SWIMMING, YOGA, HIIT, … (open enum)"],
        ["CardioType",    "TREADMILL, OUTDOOR"],
        ["Gender",        "MALE, FEMALE"],
        ["UserRole",      "USER, ADMIN"],
    ],
    col_widths=[4.0, 12.5]
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  4. REST API REFERENCE
# ══════════════════════════════════════════════════════════════════════════════
heading1("4.  REST API Reference")
divider()
body("Base URL: http://localhost:8080/api    All secured endpoints require: Authorization: Bearer <token>  and  X-User-ID: <userId>")

heading2("4.1  Authentication  —  /api/auth")
add_table(
    ["Method", "Path", "Auth", "Description"],
    [
        ["POST", "/auth/register", "None", "Register a new user. Body: { username, email, password }"],
        ["POST", "/auth/login",    "None", "Login. Returns JWT token + user object"],
    ],
    col_widths=[2.0, 4.5, 2.0, 8.0]
)

heading2("4.2  User Profile  —  /api/profile")
add_table(
    ["Method", "Path", "Description"],
    [
        ["POST", "/profile", "Create or update user profile (weight, height, age, gender, fitness goal, activity level, calorie/protein targets)"],
        ["GET",  "/profile", "Retrieve current user's profile"],
    ],
    col_widths=[2.0, 3.5, 11.0]
)

heading2("4.3  Dashboard  —  /api/dashboard")
add_table(
    ["Method", "Path", "Description"],
    [
        ["GET", "/dashboard", "Returns today's calorie/macro totals, today's and weekly activity counts, current & target weight, fitness goal, and 5 most recent food & activity entries"],
    ],
    col_widths=[2.0, 3.5, 11.0]
)

heading2("4.4  Nutrition  —  /api/nutrition")
add_table(
    ["Method", "Path", "Description"],
    [
        ["POST",   "/nutrition",          "Log a food entry (foodName, calories, protein, carbs, fat, mealType, loggedAt)"],
        ["GET",    "/nutrition",          "Fetch all food entries for the user"],
        ["GET",    "/nutrition/today",    "Fetch today's food entries"],
        ["DELETE", "/nutrition/{id}",     "Delete a food entry by ID"],
    ],
    col_widths=[2.0, 4.5, 10.0]
)

heading2("4.5  Workout  —  /api/workout")
add_table(
    ["Method", "Path", "Description"],
    [
        ["POST", "/workout/set",                   "Log a gym set (exerciseName, muscleGroup, sets, reps, weightKg, caloriesBurned, isPR)"],
        ["GET",  "/workout/sets/today",            "Fetch today's gym sets"],
        ["GET",  "/workout/sets",                  "Fetch all gym sets"],
        ["GET",  "/workout/sets/muscle/{group}",   "Fetch sets filtered by muscle group"],
        ["GET",  "/workout/exercises/progress",    "Exercise progress report (best weight per exercise)"],
        ["POST", "/workout/cardio",                "Log a cardio session (activityType, cardioType, durationMinutes, caloriesBurned, distanceKm)"],
        ["GET",  "/workout/cardio/today",          "Fetch today's cardio sessions"],
        ["GET",  "/workout/cardio",                "Fetch all cardio sessions"],
    ],
    col_widths=[2.0, 5.5, 9.0]
)

heading2("4.6  Progress  —  /api/progress")
add_table(
    ["Method", "Path", "Description"],
    [
        ["POST", "/progress/weight",       "Log a weight entry (weightKg)"],
        ["GET",  "/progress/weight",       "Fetch all weight logs"],
        ["POST", "/progress/measurements", "Log body measurements (chest, waist, hips, arms, thighs in cm)"],
        ["GET",  "/progress/measurements", "Fetch all body measurements"],
        ["GET",  "/progress/weekly",       "7-day progress report with per-day calorie, macro, and weight data"],
    ],
    col_widths=[2.0, 5.0, 9.5]
)

heading2("4.7  Calorie Summary  —  /api/calorie-summary")
add_table(
    ["Method", "Path", "Description"],
    [
        ["GET", "/calorie-summary?date=YYYY-MM-DD", "Full daily report: BMR, TDEE, calories in/out, macro targets vs actual, deficit/surplus, yesterday carry-over, and next-day recommendation text. Date defaults to today."],
    ],
    col_widths=[2.0, 6.0, 8.5]
)

heading2("4.8  Recommendations  —  /api/recommendation")
add_table(
    ["Method", "Path", "Description"],
    [
        ["POST", "/recommendation/generate",           "Generate a generic activity-based recommendation (suggestions, improvements, safety tips)"],
        ["GET",  "/recommendation/user/{userId}",      "Fetch all saved recommendations for a user"],
        ["GET",  "/recommendation/activity/{activityId}", "Fetch recommendations linked to a specific activity"],
        ["GET",  "/recommendation/daily",              "Daily recommendation: Today's Analysis (food + workout stats, bullet insights) + Tomorrow's Plan (exercise plan + Nepali meal suggestions). Never returns null — sensible defaults when no data logged."],
    ],
    col_widths=[2.0, 6.0, 8.5]
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  5. SERVICES — DETAILED
# ══════════════════════════════════════════════════════════════════════════════
heading1("5.  Backend Services — Detailed")
divider()

heading2("5.1  DailyRecommendationService")
body(
    "The core intelligence service. On every call to GET /api/recommendation/daily it:"
)
bullet("Fetches today's WorkoutSets, CardioSessions, FoodEntries, and the UserProfile from the database.")
bullet("Counts consecutive gym days to determine rest-day logic (≥3 consecutive days → rest day recommended).")
bullet("Computes total calories burned (gym + cardio), total calories consumed, total protein, and total cardio minutes.")
bullet("Reads dailyCalorieTarget and dailyProteinTarget from the user's profile.")
bullet("Calls buildAnalysisPoints() to generate 3–5 plain-English bullet insights:")
bullet("Calorie status: consumed vs goal with percentage and on-track / under / over label.", level=1)
bullet("Protein status: consumed vs target with below / on-track / above label.", level=1)
bullet("Gym summary: total sets, distinct exercises, calories burned.", level=1)
bullet("Cardio summary: sessions, minutes, calories burned.", level=1)
bullet("Net intake: consumed minus burned (shown when all data is present).", level=1)
bullet("Selects tomorrow's exercise plan based on today's activity pattern (rest day / gym+cardio / gym only / cardio only / no activity).")
bullet("Selects Nepali meal suggestions from a hardcoded catalogue based on the user's FitnessGoal and rest-day status.")
bullet("Returns a DailyRecommendationResponse with both sections — always non-null, with sensible defaults for zero-log days.")

heading2("5.2  CalorieSummaryService")
body("Orchestrates the daily calorie report via three focused sub-components:")
bullet("BmrCalculator — implements Mifflin-St Jeor formula: BMR = 10W + 6.25H − 5A ± constant. Activity multipliers: Sedentary 1.20, Lightly Active 1.375, Gym-Going 1.55, Very Active 1.725.")
bullet("MacroCalculator — adjusts the daily calorie goal by fitness goal (deficit for weight loss, surplus for gain) and splits it into protein / carbs / fat targets.")
bullet("NextDayTextBuilder — generates a natural-language next-day adjustment recommendation based on today's deficit/surplus and yesterday's carry-over.")

heading2("5.3  WeeklyProgressService")
body("Produces a 7-day rolling progress report:")
bullet("WeeklyDataFetcher — 4 bulk DB queries (food, gym sets, cardio, weight logs) for the past 7 days.")
bullet("DailyAggregator — groups records by date and computes per-day calorie intake, macro totals, calories burned, and body weight.")
bullet("WeeklySummaryBuilder — calculates 7-day averages, totals, weight change (first vs last log), and best/worst calorie days.")

heading2("5.4  Recommendation Sub-system (Activity-based)")
body("Used by the generic POST /recommendation/generate endpoint (legacy path):")
bullet("SuggestionGenerator — produces Nepali food suggestions and lifestyle tips tailored to the user's FitnessGoal (WEIGHT_LOSS / MUSCLE_GAIN / ENDURANCE / MAINTENANCE etc.) and session volume.")
bullet("ImprovementGenerator — analyses workout data and returns improvement tips (e.g., increase reps, add cardio).")
bullet("SafetyTipGenerator — returns safety reminders (warm-up, hydration, form checks) based on activity type and intensity.")

heading2("5.5  NextDayPlanService (Next-Day Plan sub-system)")
body("A separate planning engine with its own sub-components:")
bullet("NepaliMealDatabase — a static catalogue of ~30 Nepali meals (Chiura, Dal Bhat, Sukuti, Momo, Thukpa, Dhido, etc.) each tagged with suitable FitnessGoals and macro data. Exposed via a selectBest() method that picks the closest-calorie goal-appropriate option.")
bullet("MealPlanBuilder — assembles a full-day meal plan (Breakfast, Morning Snack, Lunch, Afternoon Snack, Dinner) from the NepaliMealDatabase using the user's calorie goal.")
bullet("ExercisePlanBuilder — generates tomorrow's exercise recommendation based on recent workout history.")
bullet("ReminderBuilder — generates motivational and health reminders.")

heading2("5.6  DashboardService")
body(
    "Aggregates data from multiple repositories to build the single-page dashboard response: "
    "today's calorie/macro totals from food entries, today's calories burned from activities, "
    "7-day activity count and burn, current and target body weight from the profile, "
    "and the 5 most recent food and activity log entries."
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  6. FRONTEND
# ══════════════════════════════════════════════════════════════════════════════
heading1("6.  Frontend")
divider()
body(
    "The frontend is a set of standalone HTML pages served as static files from the Spring Boot "
    "application. All pages share a common CSS design system (style.css) and three JavaScript modules."
)

heading2("6.1  Pages")
add_table(
    ["File", "Route / Purpose"],
    [
        ["landing.html",       "Marketing landing page — project introduction, CTA buttons"],
        ["index.html",         "Root redirect (to landing or dashboard based on auth state)"],
        ["login.html",         "Login form — POST /api/auth/login, stores token + userId in localStorage"],
        ["dashboard.html",     "Main dashboard — calorie ring, macro bars, recent food + activity, weight/goal summary"],
        ["nutrition.html",     "Food log — log meals by type, view today's entries, delete entries, macro breakdown"],
        ["workout-tracker.html", "Workout log — log gym sets (exercise, sets, reps, weight, muscle group) and cardio sessions"],
        ["calorie-summary.html", "Calorie deep-dive — BMR, TDEE, calorie in vs out, macro targets vs actual, deficit/surplus badge, yesterday carry-over, next-day text"],
        ["progress.html",      "Progress charts — weight log over time, body measurements, weekly activity chart"],
        ["recommendations.html", "Recommendations — Today's Analysis (stat chips + bullet insights) + Tomorrow's Plan (exercise card + Nepali meal grid)"],
        ["profile.html",       "User profile form — weight, height, age, gender, fitness goal, activity level, targets"],
        ["activities.html",    "Generic activity log (legacy)"],
        ["next-day-plan.html", "Next-day plan page (NextDayPlanService output)"],
    ],
    col_widths=[4.5, 12.0]
)

heading2("6.2  JavaScript Modules")
heading3("api.js")
body(
    "Central HTTP client. Wraps fetch() with authentication headers (Bearer token + X-User-ID). "
    "Handles 401 → auto-logout and redirect to index.html. "
    "Exposes one function per backend endpoint: api.login(), api.logFood(), api.getTodaySets(), "
    "api.getDailyRecommendation(), api.getCalorieSummary(), etc."
)
heading3("app.js")
body(
    "Shared utilities: requireAuth() (redirect-if-not-logged-in guard), setLoading() (button spinner helper), "
    "formatDate(), and other cross-page helpers."
)
heading3("sidebar.js")
body(
    "Renders the navigation sidebar (renderSidebar()) and handles active-link highlighting, "
    "mobile toggle, and logout."
)

heading2("6.3  CSS Design System (style.css)")
body(
    "CSS custom properties (--primary, --card-bg, --border, --text-muted, etc.) enable a consistent "
    "light-mode design. Components include: .btn variants, .card, .macro-bar, .meal-card, .macro-pill, "
    ".analysis-card, .stat-chip, .spinner, .empty-state, and the responsive sidebar layout (.app-layout)."
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  7. RECOMMENDATIONS MODULE — DEEP DIVE
# ══════════════════════════════════════════════════════════════════════════════
heading1("7.  Recommendations Module — Deep Dive")
divider()
body(
    "The Recommendations page is the flagship feature, combining real-time data analysis with "
    "personalised planning. It renders two distinct sections from a single API call."
)

heading2("7.1  Today's Analysis Section")
body("Displayed as a card with four stat chips and a bullet-point list:")
add_table(
    ["Stat Chip", "Content"],
    [
        ["Calories Eaten",  "Total kcal consumed today; shows % of calorie goal if profile has a target"],
        ["Protein Eaten",   "Grams of protein consumed; shows goal comparison if profile has a target"],
        ["Gym Sets Logged", "Count of workout set records logged today"],
        ["Cardio Minutes",  "Sum of all cardio session durations today (falls back to session count)"],
    ],
    col_widths=[4.0, 12.5]
)
body("Bullet insights generated by the backend (buildAnalysisPoints):")
bullet("Calorie status with on-track / under target / over target label")
bullet("Protein status with below target / on track / above target label")
bullet("Gym summary: X sets across Y exercise(s) — Z kcal burned")
bullet("Cardio summary: N session(s), M minutes — Z kcal burned")
bullet("Net intake (calories consumed minus calories burned) — only shown when all data is present")
body("When no data is logged, the section shows a motivational default message rather than empty/null values.")

heading2("7.2  Tomorrow's Plan Section")
body("Determines the next-day exercise recommendation based on today's logged activity:")
add_table(
    ["Condition", "Exercise Plan"],
    [
        ["≥3 consecutive gym days",   "Rest Day — light walking only; full rest recommended"],
        ["Gym + Cardio both today",   "Active Recovery — 20-minute walk"],
        ["Gym only today",            "Cardio — Brisk Walk or Cycling 30 min"],
        ["Cardio only today",         "Strength Training — compound lifts"],
        ["No activity today",         "Brisk Walk — 30 to 45 min"],
    ],
    col_widths=[6.0, 10.5]
)
body("Nepali meal suggestions are selected based on the user's FitnessGoal:")
add_table(
    ["Fitness Goal", "Meal Style"],
    [
        ["WEIGHT_LOSS",           "Low-calorie Nepali foods: Dhido, Gundruk soup, steamed momo, half-rice dal bhat, roasted bhatmas"],
        ["MUSCLE_GAIN / WEIGHT_GAIN", "High-protein, calorie-dense: Eggs + chiura, chicken dal bhat, sukuti, kheer + roti"],
        ["ENDURANCE",             "Carb-forward: Chiura + curd + banana, dal bhat + saag, nimbu pani + peanuts, thukpa"],
        ["MAINTENANCE / Other",   "Balanced: Roti + curd + banana, dal bhat + aloo tama, chiya + bhatmas, chicken momo"],
    ],
    col_widths=[4.5, 12.0]
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  8. CALORIE SUMMARY MODULE
# ══════════════════════════════════════════════════════════════════════════════
heading1("8.  Calorie Summary Module")
divider()
body(
    "The calorie summary is a comprehensive nutritional report for any given date (defaults to today). "
    "It is one of the most data-rich endpoints in the system."
)
heading2("8.1  Calculations Performed")
add_table(
    ["Metric", "Formula / Source"],
    [
        ["BMR",              "Mifflin-St Jeor: 10W + 6.25H − 5A + 5 (male) or −161 (female). Fallback: 1800 kcal if profile incomplete."],
        ["TDEE",             "BMR × activity multiplier (1.20 – 1.725 based on ActivityLevel)"],
        ["Daily Goal",       "TDEE adjusted by FitnessGoal (e.g., −300 for WEIGHT_LOSS, +300 for WEIGHT_GAIN)"],
        ["Macro Targets",    "Protein: 2g/kg body weight; Carbs: ~45–55% of goal calories; Fat: remainder"],
        ["Calories In",      "Sum of all FoodEntry.calories for the date"],
        ["Calories Burned",  "Sum of WorkoutSet.caloriesBurned + CardioSession.caloriesBurned for the date"],
        ["Deficit/Surplus",  "Calories In − Daily Goal; status: ON_TRACK (±150 kcal), SURPLUS, or DEFICIT"],
        ["Balance %",        "(Calories In / Daily Goal) × 100"],
        ["Yesterday Carry",  "Yesterday's deficit/surplus ÷ 3, offered as a minor next-day adjustment"],
        ["Next-Day Advice",  "Natural-language text from NextDayTextBuilder based on today + yesterday status"],
    ],
    col_widths=[4.0, 12.5]
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  9. WEEKLY PROGRESS MODULE
# ══════════════════════════════════════════════════════════════════════════════
heading1("9.  Weekly Progress Module")
divider()
body(
    "The weekly progress report covers the past 7 days. It is assembled in three passes:"
)
bullet("WeeklyDataFetcher — executes 4 batched queries: all food entries, gym sets, cardio sessions, and weight logs for [today-6 … today]. Returns a WeekData record.")
bullet("DailyAggregator — iterates over each of the 7 days, groups records by date, and builds a WeeklyProgressDay object with: total calories consumed, protein/carbs/fat grams, calories burned (gym + cardio), body weight (last log of day).")
bullet("WeeklySummaryBuilder — computes 7-day averages (avg calories, avg protein), weekly totals (total burn, total workouts), weight change (first weight entry vs last), best calorie day, and worst calorie day.")
body("The frontend renders this as an area/bar chart using the per-day data array and displays the summary metrics as headline stats.")

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  10. DATABASE CONFIGURATION
# ══════════════════════════════════════════════════════════════════════════════
heading1("10.  Database & Configuration")
divider()
heading2("10.1  application.properties")
add_table(
    ["Property", "Value"],
    [
        ["spring.datasource.url",          "jdbc:mysql://localhost:3306/fitness_db"],
        ["spring.datasource.username",     "root"],
        ["spring.jpa.hibernate.ddl-auto",  "update (schema auto-migrates on startup)"],
        ["spring.jpa.show-sql",            "true (SQL logged to console)"],
        ["spring.jpa.database-platform",   "org.hibernate.dialect.MySQLDialect"],
        ["Server port",                    "8080 (default)"],
    ],
    col_widths=[6.5, 10.0]
)

heading2("10.2  Repository Query Examples")
body("Custom JPQL queries are defined in repositories where date-range filtering is needed:")
bullet("FoodEntryRepository.findByUserIdAndDateRange(userId, start, end) — used in nutrition, dashboard, calorie summary, and daily recommendation.")
bullet("WorkoutSetRepository.findByUserIdAndDateRange(userId, start, end) — used in workout, calorie summary, and daily recommendation.")
bullet("WorkoutSetRepository.findByUserIdOrderByLoggedAtDesc(userId) — used to count consecutive gym days in the recommendation engine.")
bullet("CardioSessionRepository.findByUserIdAndDateRange(userId, start, end) — used in workout, calorie summary, and daily recommendation.")

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  11. COMPONENT INTERACTION DIAGRAM (TEXT)
# ══════════════════════════════════════════════════════════════════════════════
heading1("11.  Component Interaction — Request Flow")
divider()
body("Example: User clicks Generate Recommendation on the frontend.")

add_table(
    ["Step", "Component", "Action"],
    [
        ["1", "Browser (recommendations.html)",    "User clicks button → generateRec() → fetch GET /api/recommendation/daily with JWT + X-User-ID"],
        ["2", "JwtAuthenticationFilter",           "Validates JWT, sets SecurityContext principal"],
        ["3", "RecommendationController",          "@GetMapping /daily — reads X-User-ID header, calls dailyRecommendationService.generate(userId)"],
        ["4", "DailyRecommendationService",        "Queries 4 repositories (WorkoutSet, CardioSession, FoodEntry, UserProfile) for today's data"],
        ["5", "DailyRecommendationService",        "Builds analysis points, exercise plan, and meal suggestions"],
        ["6", "RecommendationController",          "Wraps DailyRecommendationResponse in ResponseEntity.ok(…)"],
        ["7", "Spring Security / CORS filter",     "Adds CORS headers, returns JSON response"],
        ["8", "Browser (recommendations.html)",    "renderDailyPlan(rec) renders Today's Analysis card + Tomorrow's Plan card in the DOM"],
    ],
    col_widths=[1.2, 5.0, 10.3]
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  12. RUNNING THE PROJECT
# ══════════════════════════════════════════════════════════════════════════════
heading1("12.  Running the Project")
divider()
heading2("12.1  Prerequisites")
bullet("Java 21 (JDK)")
bullet("MySQL 8.x running locally on port 3306")
bullet("Database schema created: CREATE DATABASE fitness_db;")
bullet("Maven (or use included ./mvnw wrapper)")

heading2("12.2  Start the Backend")
body("From the fitness/ directory (where pom.xml is):", indent=False)
body("    ./mvnw spring-boot:run", indent=True)
body("Or build and run the JAR:")
body("    ./mvnw clean package -DskipTests", indent=True)
body("    java -jar target/fitness-0.0.1-SNAPSHOT.jar", indent=True)
body("The server starts on http://localhost:8080")

heading2("12.3  Open the Frontend")
body(
    "Open any HTML file directly in the browser from the frontend/ directory, "
    "or serve them via any static server. "
    "The api.js file points to http://localhost:8080/api — ensure the backend is running first."
)

heading2("12.4  API Documentation")
body("Swagger UI is available at: http://localhost:8080/swagger-ui.html")

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════════════════
#  13. SUMMARY TABLE OF ALL BACKEND CLASSES
# ══════════════════════════════════════════════════════════════════════════════
heading1("13.  Complete Backend Class Inventory")
divider()
add_table(
    ["Class", "Type", "Responsibility"],
    [
        # Controllers
        ["AuthController",           "Controller", "Register, login — returns JWT"],
        ["UserProfileController",    "Controller", "Save/get user profile"],
        ["DashboardController",      "Controller", "Dashboard aggregate data"],
        ["NutritionController",      "Controller", "Log/get/delete food entries"],
        ["WorkoutController",        "Controller", "Log gym sets and cardio sessions"],
        ["ProgressController",       "Controller", "Weight logs, measurements, weekly progress"],
        ["CalorieSummaryController", "Controller", "Daily calorie summary with BMR/TDEE"],
        ["RecommendationController", "Controller", "Generic recommendations + daily recommendation"],
        ["ActivityController",       "Controller", "Legacy activity tracking"],
        ["NextDayPlanController",    "Controller", "Next-day plan (Nepali meal + exercise)"],
        # Services
        ["UserService",              "Service", "User registration, authentication, mapping"],
        ["UserProfileService",       "Service", "Profile CRUD"],
        ["DashboardService",         "Service", "Dashboard aggregation"],
        ["NutritionService",         "Service", "Food entry CRUD, mapping"],
        ["WorkoutService",           "Service", "Gym set + cardio CRUD, exercise progress"],
        ["WeightLogService",         "Service", "Weight entry CRUD"],
        ["BodyMeasurementService",   "Service", "Body measurement CRUD"],
        ["CalorieSummaryService",    "Service", "Orchestrates BMR/TDEE/macro report"],
        ["WeeklyProgressService",    "Service", "Orchestrates 7-day progress report"],
        ["DailyRecommendationService","Service","Today's Analysis + Tomorrow's Plan generator"],
        ["RecommendationService",    "Service", "Generic recommendation CRUD + generation"],
        ["ActivityService",          "Service", "Legacy activity CRUD"],
        ["NextDayPlanService",       "Service", "Next-day plan assembly"],
        # Sub-services
        ["BmrCalculator",            "Component", "Mifflin-St Jeor BMR + TDEE multiplier"],
        ["MacroCalculator",          "Component", "Daily goal and macro target calculation"],
        ["NextDayTextBuilder",       "Component", "Natural-language next-day recommendation text"],
        ["NepaliMealDatabase",       "Component", "Static catalogue of 30 Nepali meals + selectBest()"],
        ["MealPlanBuilder",          "Component", "Full-day meal plan assembly"],
        ["ExercisePlanBuilder",      "Component", "Exercise plan for next day"],
        ["ReminderBuilder",          "Component", "Motivational reminders"],
        ["SuggestionGenerator",      "Component", "Goal-aware food/lifestyle suggestions"],
        ["ImprovementGenerator",     "Component", "Workout improvement tips"],
        ["SafetyTipGenerator",       "Component", "Safety reminders"],
        ["DailyAggregator",          "Component", "Per-day calorie/macro aggregation"],
        ["WeeklyDataFetcher",        "Component", "Bulk 7-day data queries"],
        ["WeeklySummaryBuilder",     "Component", "7-day averages, totals, weight change"],
        # Security
        ["SecurityConfig",               "Config",    "Spring Security filter chain, CORS, BCrypt"],
        ["JwtUtils",                     "Component", "JWT generate, validate, extract claims"],
        ["JwtAuthenticationFilter",      "Filter",    "Per-request JWT validation, SecurityContext setup"],
        ["CustomUserDetailsService",     "Service",   "Loads UserDetails by userId for Spring Security"],
        # Other
        ["OpenAPIConfig",                "Config",    "Swagger/OpenAPI 3 configuration"],
        ["GlobalExceptionHandler",       "Advice",    "Centralised error response formatting"],
    ],
    col_widths=[5.5, 2.5, 8.5]
)

# ══════════════════════════════════════════════════════════════════════════════
#  Save
# ══════════════════════════════════════════════════════════════════════════════
out_path = r"E:\FInalProject\fitness\FitTrack_Project_Documentation.docx"
doc.save(out_path)
print(f"Saved: {out_path}")
