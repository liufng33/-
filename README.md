# Source Manager

A modern Android application for managing search and parser sources, built with Jetpack Compose and Material3.

## Features

### Source Management
- **List Sources**: View all available search and parser sources
- **Add Source**: Create new sources with validation
- **Edit Source**: Modify existing source configurations
- **Delete Source**: Remove sources with confirmation dialog
- **Switch Active Source**: Toggle active source for search or parser types

### Import Functionality
- **Import from API**: Fetch sources from a remote API endpoint
- **Import from JSON**: Load sources from local JSON content
- **Progress Feedback**: Real-time progress tracking during import
- **Error Handling**: Comprehensive error messages and recovery

## Architecture

The app follows Clean Architecture principles with three main layers:

### Domain Layer
- **Models**: `Source`, `SourceType`, `ImportResult`
- **Use Cases**: Business logic operations
  - `GetSourcesUseCase`
  - `AddSourceUseCase`
  - `UpdateSourceUseCase`
  - `DeleteSourceUseCase`
  - `SwitchActiveSourceUseCase`
  - `ImportSourcesFromApiUseCase`
  - `ImportSourcesFromJsonUseCase`
- **Repository Interface**: `SourceRepository`

### Data Layer
- **Repository Implementation**: `SourceRepositoryImpl` with in-memory storage
- **Validation**: URL format and required field validation

### UI Layer
- **ViewModels**: State management with SavedStateHandle for restoration
  - `SourceListViewModel`
  - `AddEditSourceViewModel`
  - `ImportSourceViewModel`
- **Compose Screens**: Material3 components
  - `SourceListScreen`
  - `AddEditSourceScreen`
  - `ImportSourceScreen`
- **Navigation**: Type-safe navigation with NavController
- **Theme**: Material3 dark/light theme support

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material3
- **Architecture Components**: ViewModel, Navigation, Lifecycle
- **Coroutines**: Flow for reactive data streams
- **Testing**: JUnit, MockK, Turbine for Flow testing

## Testing

The project includes comprehensive ViewModel tests:
- `SourceListViewModelTest`: Tests for listing, deleting, and switching sources
- `AddEditSourceViewModelTest`: Tests for validation and save operations
- `ImportSourceViewModelTest`: Tests for API and JSON import functionality

## State Management

- **State Restoration**: All ViewModels support configuration changes via SavedStateHandle
- **Error Handling**: User-friendly error messages with snackbar feedback
- **Loading States**: Progress indicators for async operations
- **Validation**: Real-time input validation with error messages

## Navigation Flow

```
SourceListScreen
    ├── AddSourceScreen
    ├── EditSourceScreen (with sourceId)
    └── ImportSourceScreen
```

## JSON Import Format

```json
[
  {
    "id": "unique-id",
    "name": "Source Name",
    "type": "SEARCH",
    "url": "https://example.com/api",
    "isActive": false,
    "description": "Optional description"
  }
]
```

## Building

```bash
./gradlew build
```

## Running Tests

```bash
./gradlew test
```
