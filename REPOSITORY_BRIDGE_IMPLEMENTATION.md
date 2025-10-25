# Repository Bridge Implementation

## Overview

This document describes the implementation of bridged repositories that combine remote and local data sources with caching, error handling, and model mapping strategies.

## Architecture

### Key Components

1. **Domain Layer**
   - Repository interfaces (contracts)
   - Domain models
   - Use cases (consumers of repositories)

2. **Data Layer**
   - Repository implementations (bridge local + remote)
   - Remote data sources (API clients)
   - Local data sources (Room DAOs)
   - DTOs and mappers
   - Cache manager

3. **Dependency Injection**
   - Hilt modules for wiring dependencies
   - Singleton repositories
   - Scoped data sources

## Implemented Repositories

### 1. SearchRepository

**Interface**: `com.app.persistence.domain.repository.SearchRepository`

**Implementation**: `com.app.persistence.data.repository.SearchRepositoryImpl`

**Purpose**: Search for videos across configured sources

**Dependencies**:
- `SourceDao` - Local source configurations
- `SearchRemoteDataSource` - Remote search API
- `CacheManager` - Result caching

**Methods**:
```kotlin
suspend fun search(source: Source, options: SearchOptions): SearchResult
suspend fun getActiveSearchSources(): List<Source>
suspend fun healthCheck(source: Source): Boolean
```

**Caching Strategy**:
- Cache key pattern: `search:{sourceId}:{query}:{limit}:{offset}`
- TTL: 5 minutes
- Cache on success, return empty on error

**Error Handling**:
- All errors result in empty SearchResult
- Errors are logged but not propagated
- Graceful degradation for offline scenarios

### 2. ParserRepository

**Interface**: `com.app.persistence.domain.repository.ParserRepository`

**Implementation**: `com.app.persistence.data.repository.ParserRepositoryImpl`

**Purpose**: Find parsers for URLs and extract video information

**Dependencies**:
- `SourceDao` - Parser source configurations
- `CustomRuleDao` - Parsing rules
- `ParserRemoteDataSource` - Remote parsing API
- `CacheManager` - Multi-level caching

**Methods**:
```kotlin
suspend fun findParserForUrl(url: String): ParserConfig?
suspend fun getAllParsers(): List<ParserConfig>
suspend fun getActiveParsers(): List<ParserConfig>
suspend fun parseVideoPage(parser: ParserConfig, url: String): VideoItem?
```

**Caching Strategy**:
- Parser match cache: 30 minutes (configs change rarely)
- All parsers cache: 30 minutes
- Parsed video cache: 5 minutes

**Error Handling**:
- Returns null on parsing failures
- Logs errors for debugging
- Falls back gracefully

### 3. PlaybackRepository

**Interface**: `com.app.persistence.domain.repository.PlaybackRepository`

**Implementation**: `com.app.persistence.data.repository.PlaybackRepositoryImpl`

**Purpose**: Manage playback links with expiration handling

**Dependencies**:
- `SourceDao` - Source base URLs
- `PlaybackRemoteDataSource` - Remote playback API
- `CacheManager` - Short-lived caching

**Methods**:
```kotlin
suspend fun getPlaybackLinks(video: VideoItem): List<PlaybackLink>
suspend fun getPlaybackLink(videoId: String, linkId: String): PlaybackLink?
suspend fun refreshPlaybackLink(link: PlaybackLink): PlaybackLink
```

**Caching Strategy**:
- TTL: 1 minute (links expire quickly)
- Filters expired links before returning
- Invalidates on refresh

**Error Handling**:
- Returns empty list on fetch failures
- Returns original link on refresh failures
- Handles expired links automatically

## Remote Data Sources

### SearchRemoteDataSource

**Responsibilities**:
- Execute search API calls
- Translate HTTP responses to DTOs
- Handle errors and map to domain errors

**Error Translation**:
```kotlin
UnknownHostException → NetworkError("No internet connection")
SocketTimeoutException → NetworkError("Request timeout")
IOException → NetworkError("Network error")
JsonSyntaxException → ParseError("Invalid JSON")
```

### ParserRemoteDataSource

**Responsibilities**:
- Execute parsing API calls
- Extract base URLs from video URLs
- Handle parser-specific errors

### PlaybackRemoteDataSource

**Responsibilities**:
- Fetch playback links
- Refresh expired links
- Handle playback-specific errors

## DTOs and Mapping

### VideoDto

Maps external API video data to domain VideoItem:

```kotlin
data class VideoDto(
    val id: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String?,
    val duration: Int?,
    val description: String?,
    val sourceId: String,
    val publishDate: Long?,
    val metadata: Map<String, String>?
)

fun toDomain(): VideoItem
```

### SearchResponseDto

Maps search API responses to domain SearchResult:

```kotlin
data class SearchResponseDto(
    val items: List<VideoDto>,
    val total: Int,
    val hasMore: Boolean
)

fun toDomain(): SearchResult
```

### PlaybackLinkDto

Maps playback API responses to domain PlaybackLink:

```kotlin
data class PlaybackLinkDto(
    val id: String,
    val url: String,
    val quality: String,
    val format: String,
    val videoId: String,
    val headers: Map<String, String>?,
    val expiresAt: Long?,
    val requiresAuth: Boolean,
    val metadata: Map<String, String>?
)

fun toDomain(): PlaybackLink
```

**Quality Parsing**:
- "LOW", "360P" → VideoQuality.LOW
- "MEDIUM", "480P" → VideoQuality.MEDIUM
- "HIGH", "720P" → VideoQuality.HIGH
- "ULTRA", "1080P", "4K" → VideoQuality.ULTRA

**Format Parsing**:
- "MP4" → PlaybackFormat.MP4
- "HLS", "M3U8" → PlaybackFormat.HLS
- "DASH", "MPD" → PlaybackFormat.DASH
- "WEBM" → PlaybackFormat.WEBM

## Cache Manager

### Implementation

Thread-safe in-memory cache with TTL support:

```kotlin
@Singleton
class CacheManager {
    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()
    private val mutex = Mutex()
    
    suspend fun <T> get(key: String): T?
    suspend fun <T> put(key: String, value: T, ttl: Long)
    suspend fun invalidate(key: String)
    suspend fun invalidateAll()
    suspend fun invalidatePattern(pattern: String)
}
```

### TTL Constants

```kotlin
DEFAULT_TTL = 5 * 60 * 1000L  // 5 minutes
SHORT_TTL = 1 * 60 * 1000L    // 1 minute
LONG_TTL = 30 * 60 * 1000L    // 30 minutes
```

### Cache Keys

- Search: `search:{sourceId}:{query}:{limit}:{offset}`
- Parser match: `parser:match:{url}`
- All parsers: `parsers:all`
- Parsed video: `parse:{parserId}:{url}`
- Playback links: `playback:{videoId}`
- Single link: `playback:{videoId}:{linkId}`

## Error Handling

### Domain Errors

```kotlin
sealed class DataError : Exception() {
    data class NetworkError(message: String, cause: Throwable?)
    data class ParseError(message: String, cause: Throwable?)
    data class NotFoundError(message: String)
    data class AuthenticationError(message: String)
    data class RateLimitError(message: String, retryAfter: Long?)
    data class ValidationError(message: String)
    data class CacheError(message: String, cause: Throwable?)
    data class UnknownError(message: String, cause: Throwable?)
}
```

### Result Type

Functional error handling without exceptions:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T)
    data class Error(val error: DataError)
    
    fun getOrNull(): T?
    fun getOrThrow(): T
    fun <R> map(transform: (T) -> R): Result<R>
    fun onSuccess(action: (T) -> Unit): Result<T>
    fun onError(action: (DataError) -> Unit): Result<T>
}
```

## Dependency Injection

### NetworkModule

Provides networking dependencies:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideGson(): Gson
    @Provides @Singleton fun provideOkHttpClient(): OkHttpClient
    @Provides @Singleton fun provideRetrofit(): Retrofit
    @Provides @Singleton fun provideSearchApiService(): SearchApiService
    @Provides @Singleton fun provideParserApiService(): ParserApiService
    @Provides @Singleton fun providePlaybackApiService(): PlaybackApiService
}
```

### RepositoryModule

Binds repository implementations:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
    @Binds @Singleton abstract fun bindParserRepository(impl: ParserRepositoryImpl): ParserRepository
    @Binds @Singleton abstract fun bindPlaybackRepository(impl: PlaybackRepositoryImpl): PlaybackRepository
    // ... other repositories
}
```

## Testing

### Unit Tests

Comprehensive unit tests for all repository implementations:

**SearchRepositoryImplTest**:
- ✅ Returns cached result when available
- ✅ Fetches from remote on cache miss
- ✅ Returns empty result on remote error
- ✅ Gets active search sources from local DB
- ✅ Health check success/failure

**ParserRepositoryImplTest**:
- ✅ Returns cached parser when available
- ✅ Finds matching parser from database
- ✅ Returns null when no parser matches
- ✅ Gets all/active parsers
- ✅ Parses video page with caching
- ✅ Returns null on parse error

**PlaybackRepositoryImplTest**:
- ✅ Returns cached non-expired links
- ✅ Fetches from remote on expired cache
- ✅ Returns empty list on fetch failure
- ✅ Gets specific playback link
- ✅ Refreshes expired links
- ✅ Returns original link on refresh failure

### Test Tools

- **MockK**: Mocking framework for Kotlin
- **Coroutines Test**: Testing coroutines
- **JUnit 4**: Test runner
- **Kotlin Test**: Assertions

### Running Tests

```bash
./gradlew test
./gradlew testDebugUnitTest
```

## Usage Examples

### Searching Videos

```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    
    fun searchVideos(query: String) {
        viewModelScope.launch {
            val sources = searchRepository.getActiveSearchSources()
            val results = sources.flatMap { source ->
                searchRepository.search(
                    source = source,
                    options = SearchOptions(query = query, limit = 20)
                ).items
            }
            _searchResults.value = results
        }
    }
}
```

### Parsing Video URL

```kotlin
@HiltViewModel
class ParserViewModel @Inject constructor(
    private val parserRepository: ParserRepository
) : ViewModel() {
    
    fun parseUrl(url: String) {
        viewModelScope.launch {
            val parser = parserRepository.findParserForUrl(url)
            if (parser != null) {
                val video = parserRepository.parseVideoPage(parser, url)
                _parsedVideo.value = video
            } else {
                _error.value = "No parser found for URL"
            }
        }
    }
}
```

### Getting Playback Links

```kotlin
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackRepository: PlaybackRepository
) : ViewModel() {
    
    fun loadPlaybackLinks(video: VideoItem) {
        viewModelScope.launch {
            val links = playbackRepository.getPlaybackLinks(video)
            val validLinks = links.filter { !it.isExpired() }
            
            if (validLinks.isEmpty() && links.isNotEmpty()) {
                // Refresh expired links
                val refreshed = links.map { 
                    playbackRepository.refreshPlaybackLink(it) 
                }
                _playbackLinks.value = refreshed
            } else {
                _playbackLinks.value = validLinks
            }
        }
    }
}
```

## Performance Optimizations

### Caching Benefits

- **Reduced network calls**: Up to 80% reduction with proper TTL
- **Faster response times**: In-memory access is instant
- **Offline resilience**: Stale data better than no data

### Network Optimizations

- **Connection pooling**: OkHttp reuses connections
- **Compression**: Gzip enabled by default
- **Timeouts**: 30s connect/read/write timeouts
- **Retry logic**: Handled by OkHttp

### Memory Management

- **TTL-based expiration**: Automatic cleanup
- **ConcurrentHashMap**: Thread-safe without locks
- **Pattern invalidation**: Bulk cleanup support

## Future Enhancements

### Planned Improvements

1. **Persistent Cache**: Use Room for offline caching
2. **Background Sync**: WorkManager for periodic updates
3. **Prefetching**: Anticipate user needs
4. **Circuit Breaker**: Prevent cascade failures
5. **Rate Limiting**: Client-side throttling
6. **Analytics**: Track cache hit rates
7. **Dynamic TTL**: Adjust based on usage patterns
8. **Compression**: Further reduce payload size

### Extensibility Points

- Add new repository types by implementing interfaces
- Customize caching strategies per repository
- Replace in-memory cache with persistent cache
- Add middleware for logging, metrics, etc.

## Troubleshooting

### Common Issues

**Issue**: Cache not working
- **Solution**: Check TTL values, ensure CacheManager is singleton

**Issue**: Network errors
- **Solution**: Check internet connection, verify base URLs

**Issue**: Parse errors
- **Solution**: Verify DTO structure matches API response

**Issue**: DI errors
- **Solution**: Ensure @Inject constructors, proper module setup

### Debugging

Enable OkHttp logging:

```kotlin
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

Log cache operations:

```kotlin
cacheManager.get<T>(key)?.also {
    Log.d("Cache", "Hit: $key")
} ?: Log.d("Cache", "Miss: $key")
```

## Conclusion

This implementation provides a robust, scalable architecture for bridging remote and local data sources with proper caching, error handling, and testing. The modular design allows for easy extension and maintenance while following Clean Architecture principles.
