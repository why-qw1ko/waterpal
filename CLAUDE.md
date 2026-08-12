# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WaterPal (水友) is a social drinking-water reminder app with a Spring Boot backend and Android client. The backend provides user auth (JWT), friend management, reminder sending with Firebase push notifications, and an OpenAPI/Swagger UI. The Android client uses Retrofit for networking and Firebase Cloud Messaging for push notifications.

**Test credentials:** Any 11-digit phone number + verification code `1234`.

## Codebase Structure

```
waterpal/
├── waterpal-server/               # Spring Boot 3.2 backend (Java 17)
│   ├── src/main/java/com/waterpal/server/
│   │   ├── config/                # SecurityConfig, FirebaseConfig, GlobalExceptionHandler
│   │   ├── controller/            # AuthController, FriendController, ReminderController, UserController
│   │   ├── service/               # AuthService, FriendService, ReminderService
│   │   ├── repository/            # MyBatis-Plus mappers (UserMapper, FriendMapper, ReminderMapper) + VOs
│   │   ├── entity/                # User, Friend, Reminder
│   │   ├── dto/                   # LoginRequest, LoginResponse, ApiResponse, SendReminderRequest
│   │   ├── filter/                # JwtAuthenticationFilter
│   │   └── util/                  # JwtUtil
│   ├── src/main/resources/
│   │   ├── application.yml        # DB, JWT, MyBatis-Plus, Firebase, SpringDoc config
│   │   └── schema.sql             # (referenced by docker-compose) Database init script
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yml         # MySQL 8.0 + backend stack
│   └── deploy.sh
├── waterpal-android/              # Android app (Java 17, compileSdk 34, minSdk 24)
│   ├── app/
│   │   ├── src/main/java/com/waterpal/app/
│   │   │   ├── ui/activity/       # LoginActivity, MainActivity, SettingsActivity
│   │   │   ├── ui/fragment/       # MessagesFragment, ProfileFragment, FriendsFragment
│   │   │   ├── ui/adapter/        # FriendAdapter, ReminderAdapter, SwipeController
│   │   │   ├── network/           # ApiClient (Retrofit), ApiService
│   │   │   ├── model/             # DTOs mirroring server (ApiResponse, Friend, Reminder, etc.)
│   │   │   ├── service/           # WaterPalMessagingService (Firebase FCM)
│   │   │   └── util/              # PreferenceManager, ThemeManager
│   │   ├── build.gradle
│   │   ├── google-services.json   # Firebase config
│   │   └── AndroidManifest.xml
│   └── settings.gradle
├── .github/workflows/backend-ci.yml
├── docs/DEPLOYMENT.md
└── README.md
```

## Common Commands

### Backend (waterpal-server)

```bash
# Local dev server (requires local MySQL 8.0)
cd waterpal-server
mvn spring-boot:run

# Build (skip tests, as no test suite exists)
cd waterpal-server
mvn clean package -DskipTests

# Run Docker stack (MySQL + backend)
cd waterpal-server
docker compose up -d

# Check running containers / logs
docker compose ps
docker compose logs -f backend

# Stop Docker stack
docker compose down
```

### Android (waterpal-android)

- Open `waterpal-android/` in **Android Studio** to build and run.
- The app uses ViewBinding (not Jetpack Compose).
- Modify `ApiClient.java` `BASE_URL` for emulator (`10.0.2.2:8080`) or real device (machine LAN IP).

### API Testing

```bash
# Login (no auth required)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"1234"}'

# Swagger UI
# http://localhost:8080/swagger-ui.html
```

### CI/CD

```bash
# GitHub Actions runs on push to main/develop and PRs to main
# Triggers: build (mvn clean package -DskipTests) + Docker push (main only)
```

## Key Architecture Notes

- **Auth flow:** Stateless JWT. `JwtAuthenticationFilter` intercepts requests, validates token, sets authentication. `SecurityConfig` defines public endpoints (`/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`) vs. protected endpoints (`/api/**`).
- **Database:** MyBatis-Plus with logical delete (`deleted` field). Entity → Mapper → Service → Controller layers. VOs (`FriendVO`, `ReminderVO`) separate query results from entities.
- **Push notifications:** Backend uses Firebase Admin SDK to send data messages to device FCM tokens stored in the `user` table. Android side uses `WaterPalMessagingService` (FirebaseMessagingService) to receive and display notifications.
- **Android app architecture:** Activity → Fragment → Adapter with ViewBinding. Retrofit + Gson for HTTP. OkHttp logging interceptor enabled in debug. User session stored in `PreferenceManager` (SharedPreferences wrapper for phone/token).
- **No automated test suite** — neither backend nor Android has unit/integration tests configured. The CI pipeline skips tests.
