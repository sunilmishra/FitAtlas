# FitAtlas - Modern Android Development Showcase

FitAtlas is a high-performance Android application built to demonstrate modern development practices, clean architecture, and the latest Jetpack libraries. It provides users with a comprehensive library of muscle groups and exercises, leveraging a robust offline-first approach.

## 🚀 Key Features
- **Body Part Library**: Explore exercises categorized by muscle groups (Chest, Back, Calves, etc.).
- **Exercise Database**: Search and view detailed instructions for hundreds of exercises.
- **Offline First**: Seamlessly browse previously loaded data without an internet connection using Room persistence and Flow-based local observation.
- **Rich Media**: Exercise demonstrations powered by **ExoPlayer** for video playback and **Coil** for high-quality image loading.

## 🏗 Architecture
The project follows **Clean Architecture** principles combined with a modern **MVI (Model-View-Intent)** presentation layer:

- **Unidirectional Data Flow**: The UI emits `ViewAction` intents, the ViewModel updates a single `ViewState`, and one-time events are handled via `ViewEffect`.
- **Repository Pattern**: Orchestrates data flow between a Retrofit-powered remote source and a Room-powered local source.
- **Reactive Stream**: Uses Kotlin `Flow` to propagate data changes from the database all the way to the UI.

## 🛠 Tech Stack
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Concurrency**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **UI State Management**: MVI Architecture with `StateFlow` and `SharedFlow`.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) with OkHttp for API communication.
- **Local Persistence**: [Room](https://developer.android.com/training/data-storage/room) for caching and offline support.
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Media**: [ExoPlayer](https://exoplayer.dev/) (Video) and [Coil](https://coil-kt.github.io/coil/) (Images).
- **API Source**: [ExerciseDB](https://v2.exercisedb.dev/docs) for exercise data.

## 🧪 Testing Strategy
The project emphasizes reliability through high test coverage of the business and presentation logic:
- **Unit Testing**: Implemented using `JUnit4` and `MockK`.
- **Repository Tests**: Verifies caching logic and ensures the local source acts as the single source of truth.
- **ViewModel Tests**: Uses `StandardTestDispatcher` and `runTest` to verify `UiState` transitions and navigation `ViewEffects`.
- **Test Suite**: A centralized `SuiteTest.kt` is provided to execute all repository and ViewModel tests in a single run.

## 📸 Screenshots
Refer to the visual representation of the app in the `screenshots` folder.

---