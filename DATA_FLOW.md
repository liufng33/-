# Data Flow Documentation

## Overview

This document describes the data flow architecture for the repository layer, which bridges remote and local data sources with caching, error handling, and model mapping strategies.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│                  (ViewModels, UI, UseCases)                  │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer                              │
│     (Repository Interfaces, Domain Models, Business Logic)   │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Data Layer                                │
│         (Repository Implementations, Data Sources)           │
│                                                              │
│  ┌──────────────┐   ┌─────────────┐   ┌──────────────┐    │
│  │  Local Data  │   │   Remote    │   │    Cache     │    │
│  │   Source     │   │ Data Source │   │   Manager    │    │
│  │  (Room DB)   │   │  (Retrofit) │   │ (In-Memory)  │    │
│  └──────────────┘   └─────────────┘   └──────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## Repository Implementations

### 1. SearchRepository

**Purpose**: Handles video search operations across configured sources.

**Data Flow**:

```
User Query → SearchRepository → Cache Check
                    ↓
              Cache Hit? ─(yes)→ Return Cached Result
                    ↓ (no)
              Remote API Call
                    ↓
         Transform DTO → Domain Model
                    ↓
         Store in Cache (5 min TTL)
                    ↓
              Return Result
```

**Components**:
- `SearchRepositoryImpl`: Main implementation
- `SearchRemoteDataSource`: Handles API calls
- `SourceDao`: Local source configuration
- `CacheManager`: TTL-based caching

**Caching Strategy**:
- Cache key: `search:{sourceId}:{query}:{limit}:{offset}`
- TTL: 5 minutes (DEFAULT_TTL)
- Invalidation: Time-based expiration

**Error Handling**:
- Network errors → Return empty result
- Parse errors → Return empty result
- Authentication errors → Return empty result
- Rate limit errors → Return empty result

### 2. ParserRepository

**Purpose**: Finds appropriate parsers and extracts video information from URLs.

**Data Flow**:

```
Video URL → ParserRepository → Cache Check (Parser Match)
                    ↓
              Cache Hit? ─(yes)→ Use Cached Parser
                    ↓ (no)
         Query Local DB for Parsers
                    ↓
         Match URL Pattern (Regex)
                    ↓
         Load Parser Rules from DB
                    ↓
         Cache Parser Config (30 min TTL)
                    ↓
         Call Remote Parse API
                    ↓
         Transform DTO → Domain Model
                    ↓
         Cache Parsed Video (5 min TTL)
                    ↓
         Return VideoItem
```

**Components**:
- `ParserRepositoryImpl`: Main implementation
- `ParserRemoteDataSource`: Handles parsing API calls
- `SourceDao`: Parser source configuration
- `CustomRuleDao`: Parsing rules
- `CacheManager`: Multi-level caching

**Caching Strategy**:
- Parser match cache: `parser:match:{url}` (30 min TTL)
- All parsers cache: `parsers:all` (30 min TTL)
- Parsed video cache: `parse:{parserId}:{url}` (5 min TTL)

**Error Handling**:
- URL pattern mismatch → Return null
- Parse failures → Return null
- Network errors → Return null

### 3. PlaybackRepository

**Purpose**: Retrieves and manages playback links for videos.

**Data Flow**:

```
Video Request → PlaybackRepository → Cache Check
                    ↓
         Check Link Expiration
                    ↓
         Valid Cache? ─(yes)→ Return Cached Links
                    ↓ (no)
         Query Source Config
                    ↓
         Call Remote Playback API
                    ↓
         Transform DTO → Domain Model
                    ↓
         Cache Links (1 min TTL)
                    ↓
         Return PlaybackLinks
```

**Refresh Flow**:

```
Expired Link → refreshPlaybackLink()
                    ↓
         Invalidate Caches
                    ↓
         Call Refresh API
                    ↓
         Transform & Cache New Link
                    ↓
         Return Refreshed Link
```

**Components**:
- `PlaybackRepositoryImpl`: Main implementation
- `PlaybackRemoteDataSource`: Handles playback API calls
- `SourceDao`: Source base URL
- `CacheManager`: Short-lived caching

**Caching Strategy**:
- Video links cache: `playback:{videoId}` (1 min TTL)
- Single link cache: `playback:{videoId}:{linkId}` (1 min TTL)
- Short TTL due to link expiration

**Error Handling**:
- Expired links → Auto-refresh or fetch new
- Network errors → Return empty list
- Refresh failures → Return original link

## Model Transformations

### DTO → Domain Mapping

**VideoDto → VideoItem**:
```kotlin
VideoDto.toDomain() {
    VideoItem(
        id, title, url, thumbnailUrl,
        duration, description, sourceId,
        publishDate, metadata
    )
}
```

**PlaybackLinkDto → PlaybackLink**:
```kotlin
PlaybackLinkDto.toDomain() {
    PlaybackLink(
        id, url,
        parseQuality(quality),
        parseFormat(format),
        videoId, headers,
        expiresAt, requiresAuth, metadata
    )
}
```

**SearchResponseDto → SearchResult**:
```kotlin
SearchResponseDto.toDomain() {
    SearchResult(
        items.map { it.toDomain() },
        total, hasMore
    )
}
```

### Entity → Domain Mapping

**SourceEntity → Source**:
```kotlin
SourceEntity.toDomain() {
    Source(
        id, name,
        SourceType.valueOf(type),
        baseUrl, parserClass,
        isEnabled, priority,
        parseMetadata(metadata),
        createdAt, updatedAt
    )
}
```

**CustomRuleEntity → ParseRule**:
```kotlin
CustomRuleEntity.toDomain() {
    ParseRule(
        id, name,
        RuleType.valueOf(ruleType),
        pattern, replacement,
        priority, isEnabled
    )
}
```

## Error Handling

### Error Translation

All remote data sources translate exceptions into domain errors:

```kotlin
translateException(e: Exception): DataError {
    when (e) {
        UnknownHostException → NetworkError
        SocketTimeoutException → NetworkError
        IOException → NetworkError
        JsonSyntaxException → ParseError
        else → UnknownError
    }
}
```

### HTTP Error Codes

```kotlin
handleHttpError(code: Int): DataError {
    when (code) {
        401, 403 → AuthenticationError
        404 → NotFoundError
        429 → RateLimitError
        400..499 → ValidationError
        500..599 → NetworkError
        else → UnknownError
    }
}
```

### Result Type

Repositories use a Result monad for error handling:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T)
    data class Error(val error: DataError)
}
```

## Caching Strategy

### Cache Manager

Thread-safe in-memory cache with TTL:

```kotlin
CacheManager {
    - DEFAULT_TTL: 5 minutes
    - SHORT_TTL: 1 minute
    - LONG_TTL: 30 minutes
    
    Methods:
    - get<T>(key): T?
    - put<T>(key, value, ttl)
    - invalidate(key)
    - invalidatePattern(regex)
}
```

### TTL Guidelines

| Data Type | TTL | Reason |
|-----------|-----|--------|
| Search results | 5 min | Moderate volatility |
| Parser configs | 30 min | Rarely change |
| Parsed videos | 5 min | Balance freshness/performance |
| Playback links | 1 min | Can expire quickly |
| Source configs | Long-lived | From local DB (Flow) |

### Cache Invalidation

- **Time-based**: Automatic expiration via TTL
- **Manual**: Explicit invalidation on updates
- **Pattern-based**: Invalidate multiple related keys

```kotlin
// Invalidate all search results for a source
cacheManager.invalidatePattern("search:$sourceId:.*")

// Invalidate specific playback link
cacheManager.invalidate("playback:$videoId:$linkId")
```

## Dependency Injection

### Hilt Modules

**NetworkModule**:
- Provides Retrofit, OkHttp, Gson
- Configures API services
- Sets up logging and timeouts

**DatabaseModule**:
- Provides Room database
- Provides DAOs

**RepositoryModule**:
- Binds repository implementations to interfaces
- All repositories are Singletons

### Injection Graph

```
Application
    ↓
NetworkModule → ApiServices
    ↓
DatabaseModule → DAOs
    ↓
CacheManager (Singleton)
    ↓
RemoteDataSources (ApiServices)
    ↓
RepositoryModule → Repositories (DAOs + RemoteDataSources + CacheManager)
    ↓
ViewModels / UseCases
```

## Testing Strategy

### Unit Tests

**Repository Tests**:
- Mock DAOs, RemoteDataSources, CacheManager
- Test caching behavior
- Test error handling
- Test model transformations

**Coverage Areas**:
- Cache hit scenarios
- Cache miss scenarios
- Remote fetch success
- Remote fetch failure
- Error translation
- Model mapping
- Expiration handling

### Test Tools

- **MockK**: Mocking framework
- **Kotlin Coroutines Test**: Async testing
- **JUnit**: Test runner
- **AssertK / Kotlin Test**: Assertions

## Best Practices

### Repository Implementation

1. **Always check cache first** before remote calls
2. **Cache successful responses** with appropriate TTL
3. **Handle errors gracefully** - return empty/default values
4. **Use suspend functions** for async operations
5. **Map DTOs to domain models** immediately after fetch

### Caching

1. **Choose TTL based on data volatility**
2. **Invalidate on updates** to maintain consistency
3. **Filter expired data** before returning cached results
4. **Use pattern invalidation** for bulk updates

### Error Handling

1. **Never throw in repositories** - use Result type
2. **Translate all exceptions** to domain errors
3. **Log errors** for debugging
4. **Return safe defaults** on errors

### Testing

1. **Mock all dependencies** for unit tests
2. **Test both success and failure** paths
3. **Verify cache interactions**
4. **Test edge cases** (expired links, null responses)

## Performance Considerations

### Optimization Strategies

1. **Multi-level caching**: Parser configs, search results, playback links
2. **Lazy loading**: Only fetch when needed
3. **Connection pooling**: OkHttp client reuse
4. **Timeout configuration**: Prevent hanging requests
5. **Flow for local data**: Reactive updates from DB

### Monitoring Points

- Cache hit/miss rates
- API response times
- Error rates by type
- TTL effectiveness
- Memory usage of cache

## Future Enhancements

1. **Persistent cache**: Use Room for offline support
2. **Background sync**: Proactive cache refresh
3. **Analytics**: Track usage patterns
4. **Dynamic TTL**: Adjust based on usage
5. **Circuit breaker**: Prevent cascade failures
6. **Rate limiting**: Client-side throttling
7. **Prefetching**: Anticipate user needs
8. **Compression**: Reduce network payload
