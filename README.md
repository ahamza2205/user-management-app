# User Management App

A production-quality Android application that lets you add and browse local user profiles. Built with modern Android development practices, clean architecture, and Jetpack Compose.

---

## Features

- **Add User** — Input name, age, job title, and gender
- **Field-level validation** — Real-time inline error messages per field; Save button disabled until the form is valid
- **Local persistence** — All data stored on-device using Room (SQLite); no backend required
- **Users List** — Browse all saved profiles in a scrollable list with initials avatars
- **Live count** — TopAppBar dynamically shows the number of saved users
- **Empty state** — Dedicated UI when no users have been added yet
- **Dark mode** — Full Material 3 light and dark theme support
- **Keyboard UX** — Next/Done IME actions with automatic focus traversal between fields

---

## Architecture

The project follows **Clean Architecture** principles, layered into three distinct boundaries:

```
Presentation  →  Domain  ←  Data
```

- **Data layer** — Room entity, DAO, `AppDatabase`, mappers, `UserRepositoryImpl`
- **Domain layer** — Pure Kotlin `User` model, `UserRepository` interface, use cases
- **Presentation layer** — `ViewModel`s, immutable UI state, Compose screens

### Package Structure

```
com.aa.usermanagementapp
├── data
│   ├── local           # UserEntity, UserDao, AppDatabase
│   ├── mapper          # Entity ↔ Domain extension functions
│   └── repository      # UserRepositoryImpl
├── di                  # DatabaseModule, RepositoryModule (Hilt)
├── domain
│   ├── model           # User (pure Kotlin)
│   ├── repository      # UserRepository interface
│   └── usecase         # InsertUserUseCase, GetUsersUseCase
├── presentation
│   ├── adduser         # AddUserScreen, AddUserViewModel, AddUserUiState, AddUserEvent
│   ├── navigation      # Screen, AppNavHost
│   └── users           # UsersScreen, UsersViewModel, UsersUiState
└── ui
    └── theme           # Color, Type, Theme
```

---

## Tech Stack

| Technology | Purpose | Version |
|---|---|---|
| Kotlin | Primary language | 2.0.21 |
| Jetpack Compose | UI toolkit | BOM 2025.04.00 |
| Material 3 | Design system | via Compose BOM |
| Hilt | Dependency injection | 2.56.1 |
| Room | Local SQLite database | 2.7.1 |
| Navigation Compose | In-app navigation | 2.9.0 |
| Kotlin Coroutines | Async / Flow | 1.10.2 |
| ViewModel + StateFlow | State management | Lifecycle 2.9.0 |
| KSP | Annotation processing | 2.0.21-1.0.28 |

---

## Setup

### Requirements

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android SDK — min API 24 (Android 7.0)

### Steps

```bash
git clone https://github.com/ahamza2205/user-management-app.git
```

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Run on an emulator or physical device (API 24+)

No API keys, environment variables, or external services required.

---

## Technical Decisions

### KSP over kapt
KSP (Kotlin Symbol Processing) replaces kapt for annotation processing. It is significantly faster and natively supports Kotlin. Both Hilt and Room fully support KSP, so there is no reason to use the older kapt pipeline.

### Dynamic color disabled
Material 3 dynamic color (wallpaper-based theming) is intentionally disabled. The app ships its own branded blue color palette that remains consistent across all devices and Android versions, regardless of wallpaper selection.

### Separate domain model from Room entity
`User` (domain) and `UserEntity` (data) are distinct classes connected by mapper extension functions. This enforces the rule that no Room-annotated objects ever reach the presentation layer, keeping the domain independently testable and portable.

### `@Binds` over `@Provides` in `RepositoryModule`
`@Binds` is more efficient for binding an interface to its implementation — Hilt generates no factory wrapper and no extra method body. `@Provides` is reserved for cases where manual instantiation or configuration is required.

### `stateIn(WhileSubscribed(5_000))` in `UsersViewModel`
The upstream Room `Flow` stays active for 5 seconds after the last collector. This covers Android Activity recreation (nearly instant) without leaking the database query after the screen leaves composition permanently.

### `SharedFlow` for one-time events (navigation)
`StateFlow` replays its last value to new collectors, which would cause re-navigation on screen recomposition. `SharedFlow` with no replay buffer emits events exactly once — the correct behaviour for navigation and similar side-effects.

### `isFormValid` as a computed property on `AddUserUiState`
The composable reads a single boolean — it never needs to derive state from state. All logic stays in the ViewModel; the screen remains a pure function of its input.

### `launchSingleTop = true` on navigation
Prevents duplicate destination instances in the back stack regardless of how many times the user navigates between screens (e.g. TopAppBar action + save event both triggering navigation to the same route).

---

## Time Taken

> 1 hour *(to be filled in)*

---

## Assumptions

- Single-module project — appropriate scope for this task
- No authentication or user session required
- Gender stored as a plain `String` — the three options (`Male`, `Female`, `Other`) are defined as a screen-level constant, easily extended
- No edit or delete functionality was specified — not included
- `exportSchema = false` in Room — no migration history required for version 1
- Minimum SDK API 24 covers 99%+ of active Android devices
