# Usage Examples

## Repository Usage Examples

### 1. Getting All Search Sources

```kotlin
class SourceListViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
) : ViewModel() {
    
    val searchSources: StateFlow<List<SourceConfig>> = sourceRepository
        .getAllSearchSources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
```

### 2. Adding a New Search Source

```kotlin
viewModelScope.launch {
    val newSource = SourceConfig(
        id = UUID.randomUUID().toString(),
        name = "新搜索源",
        apiEndpoint = "https://api.example.com/search",
        type = SourceType.SEARCH_SOURCE,
        parsingRules = emptyList(),
        isEnabled = true,
        priority = 10
    )
    
    sourceRepository.addSearchSource(newSource)
}
```

### 3. Toggling Source Enable Status

```kotlin
fun toggleSourceEnabled(sourceId: String, currentlyEnabled: Boolean) {
    viewModelScope.launch {
        sourceRepository.setSearchSourceEnabled(sourceId, !currentlyEnabled)
    }
}
```

### 4. Getting Parser for Specific Domain

```kotlin
class VideoPlayerViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
) : ViewModel() {
    
    fun getParserForUrl(videoUrl: String) {
        viewModelScope.launch {
            val domain = extractDomain(videoUrl)
            sourceRepository.getParsersByDomain(domain)
                .collect { parsers ->
                    if (parsers.isNotEmpty()) {
                        val parser = parsers.first() // Use highest priority parser
                        parseVideo(videoUrl, parser)
                    }
                }
        }
    }
    
    private fun extractDomain(url: String): String {
        // Extract domain from URL
        return url.substringAfter("://").substringBefore("/")
    }
}
```

### 5. Managing Custom Parsing Rules

```kotlin
fun addCustomRule(sourceId: String) {
    viewModelScope.launch {
        val rule = ParseRule(
            id = UUID.randomUUID().toString(),
            name = "视频标题",
            ruleType = RuleType.CSS_SELECTOR,
            selector = "div.video-title",
            attribute = "text",
            isRequired = true
        )
        
        sourceRepository.addCustomRule(sourceId, rule)
    }
}
```

### 6. Refreshing Sources from API

```kotlin
fun refreshSources() {
    viewModelScope.launch {
        _isLoading.value = true
        val result = sourceRepository.refreshSourcesFromRemote()
        
        result
            .onSuccess {
                _message.value = "源更新成功"
            }
            .onFailure { error ->
                _message.value = "更新失败: ${error.message}"
            }
        
        _isLoading.value = false
    }
}
```

## Preferences Usage Examples

### 1. Observing User Preferences

```kotlin
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    val preferences: StateFlow<AppPreferences> = preferencesRepository
        .preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppPreferences()
        )
}
```

### 2. Updating Playback Settings

```kotlin
fun updatePlaybackSettings(speed: Float, quality: PlaybackQuality) {
    viewModelScope.launch {
        preferencesRepository.setPlaybackSpeed(speed)
        preferencesRepository.setPlaybackQuality(quality)
    }
}
```

### 3. Managing Last Selected Source

```kotlin
fun selectSearchSource(sourceId: String) {
    viewModelScope.launch {
        preferencesRepository.setLastSelectedSearchSource(sourceId)
    }
}

fun getLastSelectedSource(): Flow<SourceConfig?> = flow {
    preferencesRepository.preferences
        .mapNotNull { it.lastSelectedSearchSourceId }
        .distinctUntilChanged()
        .collect { sourceId ->
            val source = sourceRepository.getSearchSourceById(sourceId)
            emit(source)
        }
}
```

### 4. Theme Management

```kotlin
class ThemeManager @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    val darkMode: Flow<DarkMode> = preferencesRepository
        .preferences
        .map { it.darkMode }
        .distinctUntilChanged()
    
    suspend fun setDarkMode(mode: DarkMode) {
        preferencesRepository.setDarkMode(mode)
    }
}
```

## Data Seeding Examples

### 1. Force Refresh Initial Data

```kotlin
class InitializationViewModel @Inject constructor(
    private val dataSeeder: DataSeeder
) : ViewModel() {
    
    fun forceRefreshData() {
        viewModelScope.launch {
            dataSeeder.seedInitialData(forceRefresh = true)
        }
    }
}
```

### 2. Check if Data Already Seeded

The `DataSeeder` automatically checks if data exists before seeding. It only seeds if:
- The database is empty (no search sources and no parser sources)
- OR `forceRefresh = true` is passed

## Complete Example: Video Search Feature

```kotlin
@HiltViewModel
class VideoSearchViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    private val _searchResults = MutableStateFlow<List<VideoItem>>(emptyList())
    val searchResults: StateFlow<List<VideoItem>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val enabledSources: StateFlow<List<SourceConfig>> = sourceRepository
        .getEnabledSearchSources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val lastSelectedSourceId: Flow<String?> = preferencesRepository
        .preferences
        .map { it.lastSelectedSearchSourceId }
    
    fun searchVideos(query: String, sourceId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val source = sourceRepository.getSearchSourceById(sourceId)
                if (source != null) {
                    // Save as last selected
                    preferencesRepository.setLastSelectedSearchSource(sourceId)
                    
                    // Perform search using source configuration
                    val results = performSearch(query, source)
                    _searchResults.value = results
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun performSearch(
        query: String,
        source: SourceConfig
    ): List<VideoItem> {
        // Implement actual search logic using source.apiEndpoint
        // and source.parsingRules
        return emptyList()
    }
}
```

## Testing Examples

### 1. Testing DAO with In-Memory Database

```kotlin
@Test
fun insertAndRetrieveSource() = runTest {
    val source = SearchSourceEntity(
        id = "test_id",
        name = "Test Source",
        apiEndpoint = "https://example.com/api",
        isEnabled = true,
        priority = 10
    )
    
    dao.insertSource(source)
    val retrieved = dao.getSourceById("test_id")
    
    assertEquals(source.name, retrieved?.name)
}
```

### 2. Testing Repository with Mocks

```kotlin
@Test
fun `repository returns mapped domain models`() = runTest {
    val entity = SearchSourceEntity(
        id = "1",
        name = "Test",
        apiEndpoint = "https://test.com",
        isEnabled = true,
        priority = 0
    )
    
    coEvery { dao.getAllSources() } returns flowOf(listOf(entity))
    
    val result = repository.getAllSearchSources().first()
    
    assertEquals(1, result.size)
    assertEquals("Test", result[0].name)
    assertEquals(SourceType.SEARCH_SOURCE, result[0].type)
}
```

## Advanced Patterns

### 1. Combining Multiple Data Sources

```kotlin
class VideoSourceAggregator @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val preferencesRepository: PreferencesRepository
) {
    fun getRecommendedSources(): Flow<List<SourceConfig>> = combine(
        sourceRepository.getEnabledSearchSources(),
        preferencesRepository.preferences
    ) { sources, prefs ->
        sources.sortedByDescending { source ->
            if (source.id == prefs.lastSelectedSearchSourceId) {
                source.priority + 1000 // Boost recently used
            } else {
                source.priority
            }
        }
    }
}
```

### 2. Caching Strategy

```kotlin
class CachedVideoRepository @Inject constructor(
    private val sourceRepository: SourceRepository
) {
    private val cache = mutableMapOf<String, List<VideoItem>>()
    private val cacheDuration = 5.minutes
    
    suspend fun searchWithCache(query: String, sourceId: String): List<VideoItem> {
        val cacheKey = "$sourceId:$query"
        val cached = cache[cacheKey]
        
        if (cached != null && isCacheValid(cacheKey)) {
            return cached
        }
        
        val source = sourceRepository.getSearchSourceById(sourceId)
        val results = performSearch(query, source!!)
        cache[cacheKey] = results
        
        return results
    }
}
```
