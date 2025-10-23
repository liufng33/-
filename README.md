# Clean Architecture Android App

A modern Android application built with Kotlin, showcasing Clean Architecture principles and the latest Android development technologies.

## Architecture

This project follows Clean Architecture with a multi-module structure:

```
├── app (Presentation Layer)
│   └── UI components, ViewModels, Navigation
├── domain (Business Logic Layer)
│   └── Use Cases, Domain Models, Repository Interfaces
└── data (Data Layer)
    └── Repository Implementations, Data Sources, Database, API
```

### Architecture Layers

- **Presentation Layer (app module)**: Contains UI components built with Jetpack Compose, ViewModels, and navigation logic.
- **Domain Layer (domain module)**: Pure Kotlin module containing business logic, use cases, and domain models. Has no Android dependencies.
- **Data Layer (data module)**: Manages data from various sources (network, database, preferences) and provides implementations for repository interfaces defined in the domain layer.

## Tech Stack

### Core
- **Kotlin** - Primary programming language
- **Kotlin Coroutines** - Asynchronous programming
- **Kotlin Flow** - Reactive data streams

### Android
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material3** - Material Design 3 components
- **Navigation Compose** - Navigation framework
- **Lifecycle** - Lifecycle-aware components
- **ViewModel** - UI state management

### Dependency Injection
- **Hilt** - Dependency injection framework

### Networking
- **Retrofit** - HTTP client
- **OkHttp** - HTTP client implementation
- **Gson** - JSON serialization

### Database
- **Room** - SQLite database abstraction
- **DataStore Preferences** - Key-value storage

### Media
- **ExoPlayer (Media3)** - Media playback

### Utilities
- **Jsoup** - HTML parsing

### Quality & Testing
- **Detekt** - Static code analysis
- **JUnit4** - Unit testing
- **MockK** - Mocking framework
- **Coroutines Test** - Testing coroutines

## Project Structure

```
com.cleanarch.app
├── app/
│   ├── ui/
│   │   ├── base/
│   │   │   └── BaseViewModel.kt (Base ViewModel with MVI pattern)
│   │   ├── navigation/
│   │   │   ├── AppNavigation.kt
│   │   │   └── Screen.kt
│   │   ├── screens/
│   │   │   ├── home/
│   │   │   ├── search/
│   │   │   ├── player/
│   │   │   └── settings/
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── CleanArchApp.kt (Application class)
├── domain/
│   ├── model/
│   │   └── Result.kt (Result wrapper for operations)
│   ├── repository/
│   │   └── SampleRepository.kt
│   └── usecase/
│       └── GetSampleDataUseCase.kt
└── data/
    ├── repository/
    │   └── SampleRepositoryImpl.kt
    ├── source/
    │   ├── local/
    │   │   ├── database/
    │   │   │   ├── AppDatabase.kt
    │   │   │   ├── dao/
    │   │   │   └── entity/
    │   │   └── preferences/
    │   │       └── PreferencesManager.kt
    │   └── remote/
    │       ├── api/
    │       │   └── ApiService.kt
    │       └── RemoteDataSource.kt
    └── di/
        ├── DataModule.kt
        ├── NetworkModule.kt
        └── RepositoryModule.kt
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK with API 24+ (minimum) and API 34 (target)

### Building the Project

1. Clone the repository:
```bash
git clone <repository-url>
cd <project-directory>
```

2. Open the project in Android Studio

3. Sync Gradle:
```bash
./gradlew build
```

4. Run the app:
- Select a device/emulator
- Click Run or use `./gradlew installDebug`

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run tests for specific module
./gradlew domain:test
./gradlew data:test
./gradlew app:test

# Run static code analysis
./gradlew detekt
```

## Features

The app includes navigation stubs for the following screens:

- **Home**: Main screen with navigation to other screens
- **Search**: Search functionality placeholder
- **Player**: Media player integration (ExoPlayer ready)
- **Settings**: App settings and preferences

## Key Patterns & Practices

### MVI Pattern
The app uses a Model-View-Intent (MVI) pattern with a `BaseViewModel` that provides:
- `UiState`: Represents the current state of the UI
- `UiEvent`: User actions and events
- `UiEffect`: One-time side effects (navigation, showing toasts, etc.)

### Result Wrapper
Domain layer uses a `Result` sealed class for handling operation outcomes:
- `Result.Success<T>`: Successful operation with data
- `Result.Error`: Failed operation with exception
- `Result.Loading`: Operation in progress

### Dependency Injection
Hilt is configured with the following modules:
- `DatabaseModule`: Provides Room database and DAOs
- `NetworkModule`: Provides Retrofit, OkHttp, and API services
- `RepositoryModule`: Binds repository implementations

## Code Quality

### Detekt
Static code analysis is configured with Detekt. Configuration file: `config/detekt/detekt.yml`

Run detekt:
```bash
./gradlew detekt
```

### Testing Strategy
- **Unit Tests**: Domain layer use cases and ViewModels
- **Integration Tests**: Repository implementations
- **UI Tests**: Compose UI components (framework ready)

## CI/CD

GitHub Actions workflow is configured in `.github/workflows/ci.yml` to:
- Run static code analysis (Detekt)
- Execute unit tests
- Build the application
- Upload build reports on failure

## Configuration

### API Configuration
Update the base URL in `data/src/main/kotlin/com/cleanarch/data/di/NetworkModule.kt`:
```kotlin
private const val BASE_URL = "https://api.example.com/"
```

### Database Configuration
Database name can be changed in `data/src/main/kotlin/com/cleanarch/data/di/DataModule.kt`:
```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Ensure tests pass and detekt is clean
4. Submit a pull request

## License

This project is licensed under the Boost Software License - see the [LICENSE](LICENSE) file for details.

## Future Enhancements

- [ ] Add UI tests with Compose Testing
- [ ] Implement actual API integration
- [ ] Add more comprehensive error handling
- [ ] Implement offline-first architecture
- [ ] Add more screens and features
- [ ] Configure ProGuard rules for release builds
- [ ] Add CI/CD pipeline for automatic deployments
- [ ] Implement analytics and crash reporting
- [ ] Add localization support
- [ ] Implement biometric authentication
