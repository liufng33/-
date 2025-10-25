# Local Persistence for Sources and Configuration

This project implements local persistence for search/parser sources, custom rules, and user selections using Room database and DataStore for Android applications.

## Architecture

The project follows Clean Architecture principles with clear separation of concerns:

### Domain Layer (`domain/`)
- **Models**: Domain models representing core business entities
  - `Source`: Represents search/parser sources
  - `CustomRule`: Represents custom parsing rules
  - `UserSelection`: Represents user selections (favorites, bookmarks, history)
  - `UserPreferences`: Represents user preferences and settings
  
- **Repository Interfaces**: Contracts for data operations

### Data Layer (`data/`)
- **Entities**: Room database entities mapped to domain models
- **DAOs**: Data Access Objects for database operations
- **DataStore**: SharedPreferences alternative for lightweight settings
- **Repository Implementations**: Concrete implementations of repository interfaces

### Key Features

#### 1. Room Database
- **Database**: `AppDatabase` - Main Room database with version 1
- **Entities**:
  - `SourceEntity`: Stores search/parser sources with metadata
  - `CustomRuleEntity`: Stores custom rules with foreign key to sources
  - `UserSelectionEntity`: Stores user selections with foreign key to sources
  
- **DAOs**:
  - `SourceDao`: CRUD operations for sources with Flow support
  - `CustomRuleDao`: CRUD operations for custom rules
  - `UserSelectionDao`: CRUD operations for user selections

#### 2. DataStore Integration
- `UserPreferencesDataStore`: Type-safe preferences storage for:
  - Last selected source ID
  - Auto-play settings
  - Playback quality preferences
  - Download settings
  - Dark mode preference

#### 3. Migration Support
- `DatabaseMigrations`: Handles database schema migrations
- Schema export enabled for version control

#### 4. Data Seeding
- `DataSeeder`: Bootstrap initial data from:
  - Bundled JSON file (`assets/sources.json`)
  - Default fallback sources
  - Remote API import capability

#### 5. Dependency Injection
- Hilt modules for dependency injection:
  - `DatabaseModule`: Provides Room database and DAOs
  - `DataStoreModule`: Provides DataStore instance
  - `RepositoryModule`: Binds repository implementations

## Testing

Comprehensive unit tests for all DAOs:
- `SourceDaoTest`: Tests for source CRUD operations
- `CustomRuleDaoTest`: Tests for custom rule operations
- `UserSelectionDaoTest`: Tests for user selection operations

Tests use:
- Robolectric for Android framework simulation
- Room in-memory database for isolated testing
- Coroutines test support for async operations
- InstantTaskExecutorRule for LiveData/Flow testing

## Usage

### Injecting Repositories

```kotlin
@Inject
lateinit var sourceRepository: SourceRepository

@Inject
lateinit var customRuleRepository: CustomRuleRepository

@Inject
lateinit var userSelectionRepository: UserSelectionRepository

@Inject
lateinit var preferencesRepository: PreferencesRepository
```

### Working with Sources

```kotlin
// Get all sources
sourceRepository.getAllSources().collect { sources ->
    // Handle sources
}

// Insert a new source
val source = Source(
    name = "My Source",
    type = SourceType.SEARCH,
    baseUrl = "https://api.example.com",
    parserClass = "com.example.Parser"
)
val id = sourceRepository.insertSource(source)

// Update source enabled state
sourceRepository.updateSourceEnabled(id, false)
```

### Working with Custom Rules

```kotlin
// Get rules for a source
customRuleRepository.getRulesBySourceId(sourceId).collect { rules ->
    // Handle rules
}

// Insert a new rule
val rule = CustomRule(
    sourceId = sourceId,
    name = "Filter Rule",
    ruleType = RuleType.FILTER,
    pattern = ".*\\.mp4$"
)
customRuleRepository.insertRule(rule)
```

### Working with User Selections

```kotlin
// Get recent favorites
userSelectionRepository.getRecentSelectionsByType(
    SelectionType.FAVORITE,
    limit = 20
).collect { favorites ->
    // Handle favorites
}

// Add a favorite
val selection = UserSelection(
    sourceId = sourceId,
    itemId = "item123",
    itemType = SelectionType.FAVORITE,
    title = "My Favorite Item"
)
userSelectionRepository.insertSelection(selection)
```

### Working with Preferences

```kotlin
// Observe preferences
preferencesRepository.userPreferencesFlow.collect { prefs ->
    // Handle preferences
}

// Update preferences
preferencesRepository.updateLastSelectedSourceId(sourceId)
preferencesRepository.updatePlaybackQuality(PlaybackQuality.HIGH)
preferencesRepository.updateDarkModeEnabled(true)
```

### Data Seeding

```kotlin
@Inject
lateinit var dataSeeder: DataSeeder

// Seed initial data on first launch
lifecycleScope.launch {
    dataSeeder.seedInitialData()
}

// Import sources from JSON string
lifecycleScope.launch {
    dataSeeder.importSourcesFromJson(jsonString)
}
```

## Dependencies

- **Room**: 2.6.0 - Local database
- **DataStore**: 1.0.0 - Preferences storage
- **Coroutines**: 1.7.3 - Async operations
- **Hilt**: 2.48 - Dependency injection
- **Gson**: 2.10.1 - JSON parsing

## Database Schema

### Sources Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key (auto-generated) |
| name | String | Unique source name |
| type | String | Source type (SEARCH, PARSER, HYBRID) |
| base_url | String | Base URL for the source |
| parser_class | String | Parser class name |
| is_enabled | Boolean | Whether source is enabled |
| priority | Int | Source priority for ordering |
| metadata | String | JSON metadata |
| created_at | Long | Creation timestamp |
| updated_at | Long | Last update timestamp |

### Custom Rules Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key (auto-generated) |
| source_id | Long | Foreign key to sources |
| name | String | Rule name |
| rule_type | String | Rule type (FILTER, TRANSFORM, etc.) |
| pattern | String | Regex pattern |
| replacement | String? | Optional replacement string |
| is_enabled | Boolean | Whether rule is enabled |
| priority | Int | Rule priority |
| created_at | Long | Creation timestamp |
| updated_at | Long | Last update timestamp |

### User Selections Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key (auto-generated) |
| source_id | Long | Foreign key to sources |
| item_id | String | Item identifier |
| item_type | String | Selection type (FAVORITE, BOOKMARK, etc.) |
| title | String | Item title |
| metadata | String | JSON metadata |
| selected_at | Long | Selection timestamp |
| last_accessed_at | Long | Last access timestamp |

## Best Practices

1. **Use Flow for reactive data**: All queries return Flow for reactive updates
2. **Coroutine support**: All suspend functions for async operations
3. **Foreign key constraints**: Cascade delete for data integrity
4. **Indexed columns**: Optimized queries with strategic indexes
5. **Type converters**: JSON serialization for complex types
6. **Migration support**: Schema versioning and migration support
7. **Testing**: Comprehensive unit tests for all DAOs
