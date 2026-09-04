# Hatchery CRM (Kotlin Multiplatform + Compose Multiplatform)

Shared Kotlin codebase targeting **Android**, **iOS**, and **Desktop** (Windows/macOS/Linux),
with a starting data model for customers, hatch batches, and orders (SQLDelight).

## Structure
```
HatcheryCRM/
├── composeApp/                     # shared module
│   ├── src/commonMain/kotlin/...   # shared UI (App.kt) + repository + DB contract
│   ├── src/commonMain/sqldelight/  # Hatchery.sq — schema & queries
│   ├── src/androidMain/            # Android driver, MainActivity, manifest
│   ├── src/iosMain/                # iOS driver, MainViewController (entry point for Swift)
│   └── src/desktopMain/            # Desktop driver, main.kt (Compose Desktop window)
├── iosApp/iosApp/                  # Swift shell (ContentView.swift, iOSApp.swift)
├── settings.gradle.kts / build.gradle.kts / gradle/libs.versions.toml
```

## Opening the project
1. **Android Studio** (latest stable, e.g. Ladybug/Meerkat+) with the **Kotlin Multiplatform plugin** installed.
2. `File > Open` → select the `HatcheryCRM` folder.
3. Let Gradle sync. No wrapper jar is bundled here (offline scaffold) — Android Studio will
   offer to regenerate it on first sync; accept it, or run `gradle wrapper` once you have
   Gradle installed locally.
4. **JDK 17+** is required (project currently detected Java 11 on this machine — install/select
   JDK 17 in Android Studio: `Settings > Build Tools > Gradle > Gradle JDK`).

## Running each target
- **Android**: pick the `composeApp` run configuration with an Android device/emulator.
- **Desktop**: `./gradlew :composeApp:run`
- **iOS**: requires a Mac + Xcode. Open Android Studio's KMP plugin action
  "Open iOS project in Xcode" (or create a new Xcode project inside `iosApp/` via
  Xcode's multiplatform template) and add the generated `ComposeApp.framework` from
  `composeApp` as a dependency — the Swift files in `iosApp/iosApp/` are already wired
  to call `MainViewController()` from the shared module.

## What's included
- Shared `App.kt` composable (Material3) shown on all platforms.
- SQLDelight schema (`Hatchery.sq`) for `Customer`, `HatchBatch`, `Order_`.
- Platform-specific SQLite drivers (Android/iOS/Desktop) behind an `expect/actual` factory.
- Ktor client dependencies wired per platform (ready for REST/Supabase/Firebase integration).

## Suggested next steps
1. Add navigation (`Compose Multiplatform Navigation` or `Voyager`).
2. Flesh out CRUD screens for customers, hatch batches, and orders.
3. Add a backend (Supabase/Firebase) and sync logic using the Ktor client already added.
4. Add authentication and multi-user roles.
5. Set up CI (GitHub Actions) to build Android/Desktop artifacts; iOS build requires a macOS runner.
