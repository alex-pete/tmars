# Simple KMP app

Kotlin Multiplatform starter with shared business logic and native UI on each platform.

- Android UI is Jetpack Compose in `androidApp`. Compose is not shared with iOS.
- iOS UI is SwiftUI in `iosApp`.
- Shared Kotlin code lives in `shared` and is consumed by both apps.

The sample screen shows a platform greeting from shared code and a counter owned by a shared `HomeViewModel`. Android observes that state with Jetpack Compose. iOS observes it with SwiftUI.

## Modules

| Module | Role |
| --- | --- |
| `shared` | View models, state, and dependency injection. Android and iOS targets. |
| `androidApp` | Android application. Jetpack Compose, Material 3. |
| `iosApp` | Xcode app. SwiftUI. Builds the `Shared` framework with Gradle. |

`shared` exports a static framework named `Shared`. SwiftUI reads `HomeViewModel` through [KMP-ObservableViewModel](https://github.com/rickclephas/KMP-ObservableViewModel) and [KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines). Koin creates the shared objects.

## Run Android

Install JDK 17+ and Android SDK 35, then either open the project in Android Studio or run:

```bash
./gradlew :androidApp:assembleDebug
```

Gradle uses `ANDROID_HOME` or `local.properties` (`sdk.dir`).

## Run iOS

On a Mac with Xcode 15+:

1. Open `iosApp/iosApp.xcodeproj`.
2. Set your Team in `iosApp/Configuration/Config.xcconfig` (`TEAM_ID`).
3. Run the `iosApp` scheme. The Xcode build compiles the Kotlin framework first.

## Add a feature

1. Put state and behavior in `shared` (`HomeViewModel` is the pattern).
2. Register new types in `shared/src/commonMain/kotlin/com/jetbrains/kmpapp/di/Koin.kt`.
3. Draw the Android screen in `androidApp` with Jetpack Compose.
4. Draw the iOS screen in `iosApp` with SwiftUI, using `@StateViewModel` for shared view models.

Platform-specific APIs go in `androidMain` or `iosMain` behind an `expect`/`actual` declaration, the same way `platformName()` does.
