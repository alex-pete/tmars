# Player board

Kotlin Multiplatform tracker for a Terraforming Mars player board. Shared Kotlin holds the counts and the rules. Android draws the board with Jetpack Compose. iOS draws the same board with SwiftUI.

The screen follows the physical player mat:

- Top row: MegaCredit, steel, and titanium stock. Steel is worth 2 each. Titanium is worth 3 each.
- Next two rows: production tracks. MegaCredit production runs from −5 upward. The other tracks start at 0. The arrow steps production past 10.
- The globe is the terraform rating. Income is rating plus MegaCredit production, and it does not go below zero.
- Bottom row: plant, energy, and heat stock. Eight plants spend for a greenery and raise the rating. Eight heat spends to raise temperature and the rating. Leftover energy becomes heat when you produce.

Standard setup starts at rating 20 and production 1. Corporate Era starts at rating 20 and production 0. Produce runs the generation: energy stock moves into heat, then each resource gains its production. MegaCredits gain the income figure.

## Modules

| Module | Role |
| --- | --- |
| `shared` | `PlayerBoard`, production rules, and `BoardViewModel`. |
| `androidApp` | Landscape Jetpack Compose board. |
| `iosApp` | Landscape SwiftUI board. |

## Run Android

Install JDK 17+ and Android SDK 35, then open the project in Android Studio or run:

```bash
./gradlew :androidApp:assembleDebug
```

Gradle uses `ANDROID_HOME` or `local.properties` (`sdk.dir`).

## Run iOS

On a Mac with Xcode 15+:

1. Open `iosApp/iosApp.xcodeproj`.
2. Set your Team in `iosApp/Configuration/Config.xcconfig` (`TEAM_ID`).
3. Run the `iosApp` scheme. The Xcode build compiles the `Shared` framework first.
