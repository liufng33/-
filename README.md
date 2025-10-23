# Video Player App

An Android video player application with URL parsing and playback capabilities using Jsoup and ExoPlayer.

## Features

### URL Input Screen
- Paste video URLs from clipboard
- Choose parser source (Jsoup HTML Parser, Direct URL)
- Parse web pages to extract video streams
- Display available stream qualities
- Handle parsing errors with fallback options

### Video Player Screen
- ExoPlayer-based video playback
- Play/pause controls
- Seek forward/backward (10 seconds)
- Seek bar for precise navigation
- Auto-hide controls after 3 seconds
- Lifecycle-aware playback state management
- Error handling with user feedback

### Architecture
- **MVVM** architecture pattern
- **Jetpack Compose** for modern declarative UI
- **Hilt** for dependency injection
- **Coroutines** for asynchronous operations
- **StateFlow** for reactive state management

### Technologies
- **ExoPlayer** (Media3) for video playback
- **Jsoup** for HTML parsing and video extraction
- **Jetpack Navigation** for screen navigation
- **Material Design 3** for UI components

### Casting Support Architecture
The app is designed with casting support in mind:
- `PlayerController` is decoupled from UI
- Player instance can be accessed and controlled externally
- Playback state is managed through reactive StateFlow
- Ready for Cast SDK integration

## Testing

### Unit Tests
- `UrlInputViewModelTest` - Tests URL input and parsing logic
- `PlayerViewModelTest` - Tests player controls and state management
- `PlayerControllerTest` - Tests ExoPlayer integration
- `JsoupParserTest` - Tests HTML parsing functionality
- `DirectUrlParserTest` - Tests direct URL parsing

### Instrumentation Tests
- `UrlInputScreenTest` - UI tests for URL input screen

## Building

```bash
./gradlew build
```

## Running Tests

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/videoplayer/
│   │   │   ├── data/
│   │   │   │   ├── model/          # Data models
│   │   │   │   └── parser/         # Video parsers
│   │   │   ├── di/                 # Dependency injection
│   │   │   ├── ui/
│   │   │   │   ├── player/         # Player screen and ViewModel
│   │   │   │   ├── theme/          # App theme
│   │   │   │   └── url/            # URL input screen and ViewModel
│   │   │   ├── MainActivity.kt
│   │   │   └── VideoPlayerApp.kt
│   │   └── res/                    # Resources
│   ├── test/                       # Unit tests
│   └── androidTest/                # Instrumentation tests
└── build.gradle.kts
```

## Error Handling

- Network errors during parsing are caught and displayed to the user
- Playback errors show an overlay with error message
- Fallback URLs provided when direct parsing is not possible
- Resume playback after app lifecycle events (pause/resume)

## Lifecycle Management

- Playback state is saved on pause
- Playback position is restored on resume
- Player is properly released on destroy
- Progress updates are managed with coroutines

## License

See LICENSE file for details.
