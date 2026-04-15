# 🏋️ FitnessWebApp

> A full-stack fitness tracking web application built as a 6th semester college project.  
> Designed for gym-going individuals in Nepal to track nutrition, exercise, and body progress — with a built-in Nepali food database and daily personalized recommendations.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Pages](#pages)
- [Notes](#notes)

---

## Overview

FitnessWebApp helps gym-goers stay on top of their health goals by bringing together calorie tracking, workout logging, and progress monitoring in one place. The app is built with Nepal in mind — featuring a hardcoded database of 25 common Nepali foods with full macro breakdowns, and recommendations tailored to local dietary habits.

---

## 🛠 Tech Stack

### Backend

| Technology | Detail |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.1 |
| Spring Security + JWT | jjwt 0.13.0 |
| Spring Data JPA | ORM / database access |
| MySQL | Relational database |
| Lombok | Boilerplate reduction |
| Bean Validation | Input validation |
| SpringDoc OpenAPI | Swagger UI docs |

### Frontend

| Technology | Detail |
|---|---|
| HTML / CSS / JavaScript | Vanilla — no framework, no build tool |
| Architecture | Multi-page app (one `.html` file per page) |
| Styling | Custom CSS with CSS variables, light mode |
| API calls | Native `fetch()` |
| Auth state | `localStorage` (token, userId, name, email) |
| Dependencies | None — no npm, no bundler |

---

## ✨ Features

### 🔐 Authentication
- JWT-based login and registration
- Token stored in `localStorage`
- Unauthenticated users are automatically redirected to `index.html`

### 📊 Dashboard
- Daily calorie summary at a glance
- Gym session and cardio log overview
- Streak counter — tracks consecutive days with both food and exercise logged

### 🥗 Nutrition & Calorie Tracking
- Log daily meals with full calorie and macro breakdown
- Built-in Nepali food database (25 food items with calories, protein, carbs, fat)
- Protein, carbohydrate, and fat targets derived from user profile
- Calorie floor enforced: **1200 kcal** (women) / **1500 kcal** (men)
- BMR calculated using the **Mifflin-St Jeor** formula
- TDEE = BMR × activity multiplier, updated daily from logged exercise

### 🏋️ Workout Tracker
- Log workouts by type: Chest Day, Back Day, Leg Day, etc.
- Track sets, reps, and weight per exercise
- Progressive overload tracking — shows improvement over time
- 🏆 **PR (Personal Record) badge** on new personal bests

### 🏃 Cardio Tracker
- Log cardio sessions: treadmill vs. outdoor walk
- Different MET values applied per session type for accurate calorie burn calculation

### 💡 Recommendations
- Generated each evening based on the day's gym and cardio logs
- Suggests Nepali meal options for tomorrow with calorie and macro info
- Recommends gym plan or walking target for the next day
- ⚠️ Flags a **rest day** if 3+ consecutive gym sessions are detected (overtraining prevention)

### 📈 Progress Tracking
- Body weight and measurement history
- Plain text overview — no graphs or charts

### 🎯 Goal Validation
- Flags unsafe weight-loss goals (e.g., more than 1 kg/week)
- Minimum calorie intake enforced based on user gender

---

## 📁 Project Structure

```
FitnessWebApp/
├── fitness/                              # Spring Boot backend
│   ├── src/
│   │   └── main/java/com/project/fitness/
│   │       ├── controller/
│   │       ├── service/
│   │       │   ├── CalorieSummaryService/
│   │       │   ├── RecommendationService/
│   │       │   ├── WeeklyProgressService/
│   │       │   └── NextDayPlanService/   # Backend only — not wired to frontend yet
│   │       ├── repository/
│   │       ├── model/
│   │       └── config/
│   └── pom.xml
│
└── frontend/                             # Vanilla HTML/CSS/JS
    ├── index.html                        # Landing + Login/Signup
    ├── dashboard.html
    ├── activities.html
    ├── nutrition.html
    ├── progress.html
    ├── workout-tracker.html
    ├── recommendations.html
    ├── calorie-summary.html
    ├── profile.html
    ├── app.js                            # Shared utilities + Nepali food database
    ├── api.js                            # API client (fetch wrapper)
    ├── sidebar.js                        # Sidebar HTML template
    └── style.css                         # Global stylesheet
```

---

## 🚀 Getting Started

### Prerequisites

- Java 25
- Maven
- MySQL
- Any static file server (or simply open HTML files directly in a browser)

### Backend Setup

```bash
cd fitness
```

Configure your database in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fitnessdb
spring.datasource.username=root
spring.datasource.password=yourpassword
```

Run the backend:

```bash
./mvnw spring-boot:run
```

- **API base URL:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Frontend Setup

No build step required. Open `frontend/index.html` directly in a browser, or serve with any static server:

```bash
# Using Python
cd frontend
python -m http.server 3000
# Then open http://localhost:3000
```

> ⚠️ Make sure the backend is running at `http://localhost:8080` before using the app.

---

## 🔌 API Reference

All requests are sent to `http://localhost:8080/api`.

**Authentication headers:**
- `Authorization: Bearer <token>`
- `X-User-ID: <userId>`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT |
| `GET` | `/api/dashboard` | Dashboard summary |
| `GET/POST` | `/api/nutrition` | Food logs |
| `GET/POST` | `/api/workout/set` | Gym sets |
| `GET/POST` | `/api/workout/cardio` | Cardio logs |
| `GET` | `/api/recommendation/daily` | Daily recommendation |
| `GET` | `/api/calorie-summary` | Calorie summary |
| `GET/POST` | `/api/progress/weight` | Weight logs |

Full interactive docs available at Swagger UI once the backend is running.

---

## 🖥 Pages

| Page | File |
|---|---|
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

## 📝 Notes

- Recommendations are based solely on gym session and cardio logs — not generic activity types.
- The frontend is intentionally minimal — plain HTML/CSS/JS with no framework or component library.
- The Nepali food database is hardcoded in `app.js` with 25 common food items and full macro data.
- `NextDayPlanService` exists on the backend but is not yet connected to the frontend.



