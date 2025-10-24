# Yingshi (影视) - Video App

A local persistence implementation for an Android video application with search sources and parser sources management.

## Features

### Core Persistence Layer
- **Room Database**: Complete database schema for local data storage
  - SearchSourceEntity: Stores video search source configurations
  - ParserSourceEntity: Stores video parser configurations
  - CustomRuleEntity: Stores custom HTML parsing rules

- **DataStore**: App preferences management using Jetpack DataStore
  - Last selected sources
  - Playback preferences (speed, quality, auto-play)
  - UI preferences (dark mode)
  - User content preferences

### Data Seeding
- Automatic data seeding on first launch
- Primary source: API endpoint (https://132130.v.nxog.top/api1.php?id=3)
- Fallback: Bundled JSON file in assets
- Force refresh capability

### Repository Pattern
- Clean architecture with domain and data layers
- Repository interfaces in domain layer
- Repository implementations in data layer
- Entity-to-domain model mapping
- Coroutine Flow support for reactive data

### Dependency Injection
- Hilt modules for all components:
  - DatabaseModule: Room database and DAOs
  - NetworkModule: Retrofit API service
  - RepositoryModule: Repository implementations

## Project Structure

```
app/src/main/java/com/yingshi/video/
├── data/
│   ├── local/
│   │   ├── entity/           # Room entities
│   │   ├── dao/              # Room DAOs
│   │   ├── database/         # Database and type converters
│   │   ├── preferences/      # DataStore preferences manager
│   │   └── seeding/          # Initial data seeding logic
│   ├── remote/               # API service and response models
│   └── repository/           # Repository implementations
├── domain/
│   ├── model/                # Domain models
│   └── repository/           # Repository interfaces
└── di/                       # Hilt dependency injection modules

app/src/test/java/             # Unit tests
```

## Database Schema

### SearchSourceEntity
- id (String, Primary Key)
- name (String)
- apiEndpoint (String)
- isEnabled (Boolean)
- priority (Int)
- headers (Map<String, String>)
- description (String?)
- lastUpdated (Long)

### ParserSourceEntity
- id (String, Primary Key)
- name (String)
- parserUrl (String)
- supportedDomains (List<String>)
- isEnabled (Boolean)
- priority (Int)
- timeout (Long)
- headers (Map<String, String>)
- description (String?)
- lastUpdated (Long)

### CustomRuleEntity
- id (String, Primary Key)
- sourceId (String, Foreign Key)
- name (String)
- ruleType (String: CSS_SELECTOR, XPATH, JSON_PATH, REGEX)
- selector (String)
- attribute (String?)
- regex (String?)
- replacement (String?)
- isRequired (Boolean)
- defaultValue (String?)

## Domain Models

- **VideoItem**: Video metadata
- **SourceConfig**: Search source configuration
- **ParserConfig**: Parser source configuration
- **ParseRule**: HTML parsing rule
- **AppPreferences**: User preferences

## Key Technologies

- **Kotlin**: 1.9.0
- **Room**: 2.6.0 - Local database
- **DataStore**: 1.0.0 - Preferences storage
- **Hilt**: 2.48 - Dependency injection
- **Retrofit**: 2.9.0 - Network requests
- **Coroutines**: 1.7.3 - Async operations
- **Gson**: 2.10.1 - JSON serialization

## Testing

The project includes comprehensive unit tests:
- `SearchSourceDaoTest`: Tests for search source DAO operations
- `ParserSourceDaoTest`: Tests for parser source DAO operations
- `SourceRepositoryImplTest`: Tests for repository implementation

Test technologies:
- JUnit 4
- Robolectric 4.11.1 - Android unit testing
- MockK 1.13.8 - Mocking framework
- Coroutines Test 1.7.3

## Database Migrations

The database is configured with:
- Version 1 as initial schema
- `fallbackToDestructiveMigration()` for development
- Migration strategy example included (MIGRATION_1_2)
- Schema export enabled for version control

## DAO Operations

All DAOs provide:
- **CRUD operations**: Insert, Update, Delete, Query
- **Flow-based queries**: Reactive data streams
- **Bulk operations**: Insert/delete multiple items
- **Filtered queries**: Enabled items only, by priority
- **Status updates**: Enable/disable, priority changes

## API Integration

The app integrates with a remote API for source updates:
- Endpoint: https://132130.v.nxog.top/api1.php?id=3
- Response format: JSON with search_sources and parser_sources arrays
- Automatic mapping to local entities
- Fallback to bundled default_sources.json

## Usage

### Initialize the App
The `YingshiApplication` class automatically seeds initial data on first launch.

### Access Repositories
Inject repositories via Hilt:

```kotlin
@Inject
lateinit var sourceRepository: SourceRepository

@Inject
lateinit var preferencesRepository: PreferencesRepository
```

### Query Sources
```kotlin
// Get all search sources as Flow
sourceRepository.getAllSearchSources()
    .collect { sources ->
        // Handle sources
    }

// Get enabled parsers
sourceRepository.getEnabledParserSources()
    .collect { parsers ->
        // Handle parsers
    }
```

### Manage Preferences
```kotlin
// Observe preferences
preferencesRepository.preferences
    .collect { prefs ->
        // Handle preference changes
    }

// Update preferences
preferencesRepository.setPlaybackSpeed(1.5f)
preferencesRepository.setDarkMode(DarkMode.DARK)
```

## License

Boost Software License - Version 1.0
