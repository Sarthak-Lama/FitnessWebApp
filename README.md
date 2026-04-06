
# FitnessWebApp

A full-stack fitness tracking web application built as a 6th semester college project. Designed for gym-going individuals in Nepal to track nutrition, exercise, and body progress in one place — with Nepali food options and daily recommendations.

---

## Tech Stack

**Backend**
- Java 25
- Spring Boot 4.0.1
- Spring Security + JWT (jjwt 0.13.0)
- Spring Data JPA
- MySQL
- Lombok
- Bean Validation
- SpringDoc OpenAPI (Swagger UI)

**Frontend**
- Vanilla HTML, CSS, JavaScript — no framework, no build tool
- Multi-page app (one `.html` file per page)
- Plain custom CSS — light mode, CSS variables, no UI library
- `fetch()` for API calls
- `localStorage` for auth state (token, userId, name, email)
- No npm, no bundler, no dependencies

---

## Pages

| Page | File |
|------|------|
| Login / Landing | `index.html` |
| Dashboard | `dashboard.html` |
| Activities | `activities.html` |
| Nutrition | `nutrition.html` |
| Progress | `progress.html` |
| Workout Tracker | `workout-tracker.html` |
| Recommendations | `recommendations.html` |
| Calorie Summary | `calorie-summary.html` |
| Profile | `profile.html` |

---

## Features

### Authentication
- JWT-based login and registration
- Token stored in `localStorage`
- Unauthenticated users redirected to `index.html`

### Dashboard
- Daily calorie summary (plain text)
- Gym session and cardio log overview
- Streak counter — consecutive days with food + exercise logged

### Nutrition & Calorie Tracking
- Log daily meals with calorie and macro breakdown
- Nepali food database built-in (25 food items with calories, protein, carbs, fat)
- Protein, carbohydrate, and fat goals set from user profile
- Calorie floor enforced: 1200 kcal (women), 1500 kcal (men)
- BMR calculated using Mifflin-St Jeor formula
- TDEE = BMR × activity multiplier, updated daily from logged exercise

### Workout Tracker
- Log workouts by type: Chest Day, Back Day, Leg Day, etc.
- Track sets, reps, and weight per exercise
- Progressive overload tracking — shows improvement over time
- PR (Personal Record) badge on new personal bests

### Cardio Tracker
- Log cardio sessions (treadmill vs outdoor walk)
- Different MET values applied per type for accurate calorie calculation

### Recommendations
- Generated each evening based on the day's gym and cardio logs only
- Suggests Nepali meal options for tomorrow with calories and macros
- Recommends gym plan or walking target for next day
- Flags rest day if 3+ consecutive gym days logged (overtraining prevention)

### Progress Tracking
- Body weight and measurement history
- Plain text overview — no graphs or charts

### Goal Validation
- Flags unsafe goals (e.g., losing more than 1 kg/week)
- Minimum calorie recommendations enforced based on gender

---

## Project Structure

```
FitnessWebApp/
├── fitness/                        # Spring Boot backend
│   ├── src/
│   │   └── main/java/com/project/fitness/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── CalorieSummaryService/
│   │       │   ├── RecommendationService/
│   │       │   ├── WeeklyProgressService/
│   │       │   └── NextDayPlanService/  # Backend only, not wired to frontend yet
│   │       ├── repository/
│   │       ├── model/
│   │       └── config/
│   └── pom.xml
│
└── frontend/                       # Vanilla HTML/CSS/JS
    ├── index.html                  # Landing + Login/Signup
    ├── dashboard.html
    ├── activities.html
    ├── nutrition.html
    ├── progress.html
    ├── workout-tracker.html
    ├── recommendations.html
    ├── calorie-summary.html
    ├── profile.html
    ├── app.js                      # Shared utilities + Nepali food database
    ├── api.js                      # API client (fetch wrapper)
    ├── sidebar.js                  # Sidebar HTML template
    └── style.css                   # Global stylesheet
```

---

## Getting Started

### Prerequisites
- Java 25
- Maven
- MySQL
- Any static file server or just open HTML files directly in browser

### Backend Setup

```bash
cd fitness

# Configure database in src/main/resources/application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/fitnessdb
# spring.datasource.username=root
# spring.datasource.password=yourpassword

./mvnw spring-boot:run
```

API runs at: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend Setup

No build step needed. Open `frontend/index.html` directly in a browser, or serve with any static server:

```bash
# Using Python
cd frontend
python -m http.server 3000
# Open http://localhost:3000
```

Make sure the backend is running at `http://localhost:8080` before using the app.

---

## API

All requests go to `http://localhost:8080/api`. JWT token sent via `Authorization: Bearer <token>` header. User ID sent via `X-User-ID` header.

Key endpoints:
- `POST /api/auth/register` — Register
- `POST /api/auth/login` — Login
- `GET /api/dashboard` — Dashboard summary
- `GET/POST /api/nutrition` — Food logs
- `GET/POST /api/workout/set` — Gym sets
- `GET/POST /api/workout/cardio` — Cardio logs
- `GET /api/recommendation/daily` — Daily recommendation
- `GET /api/calorie-summary` — Calorie summary
- `GET/POST /api/progress/weight` — Weight logs

Full docs available at Swagger UI once backend is running.

---

## Notes

- Recommendations are based solely on gym session and cardio logs — no generic activity types.
- Frontend is intentionally simple — plain HTML/CSS/JS, no framework, no component library.
- Nepali food database is hardcoded in `app.js` with 25 common food items.

---
