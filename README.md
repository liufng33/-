# Remote Data Layer

A comprehensive Kotlin-based remote data layer implementation with Retrofit clients, Jsoup HTML parsing, and robust error handling.

## Features

### 🌐 Retrofit & OkHttp Configuration
- **Coroutine Support**: Full async/await support with Kotlin coroutines
- **Multiple Converters**: JSON (Gson), XML (SimpleXML), and plain text (Scalars)
- **Logging**: Built-in HTTP logging interceptor for debugging
- **Configurable Timeouts**: Customizable connection, read, and write timeouts
- **Retry Logic**: Automatic retry on connection failure

### 📦 DTOs and Domain Entities
- **ApiResponseDto**: Handles API responses from the main endpoint
- **SearchResponseDto**: Manages search results with pagination
- **Domain Entities**: Clean separation between data and domain layers
- **Type-Safe Mappers**: Null-safe mapping from DTOs to domain entities

### 🔌 Remote Data Sources
1. **ApiRemoteDataSource**: Main API endpoint integration
   - Fetch structured JSON data
   - Fetch raw string responses
   
2. **SearchRemoteDataSource**: Search functionality
   - Query with pagination support
   - Relevance scoring

3. **ParserRemoteDataSource**: HTML parsing capabilities
   - URL-based parsing
   - Direct HTML string parsing
   - Rule-driven content extraction

4. **DynamicApiDataSource**: Dynamic third-party API integration
   - Support for arbitrary URLs
   - Flexible content fetching

### 🔍 Jsoup HTML Parser
- **JsoupHtmlParser**: Basic HTML parsing
  - Title extraction
  - Content extraction
  - Metadata extraction
  - Link extraction

- **RuleBasedParser**: Advanced rule-driven parsing
  - Custom CSS selectors
  - Multiple extraction types (TEXT, HTML, ATTR)
  - Flexible parsing configurations
  - Metadata mapping

- **HtmlFetcher**: HTTP-based HTML fetching
  - User-agent configuration
  - Timeout handling
  - Error management

### ⚡ Rate Limiting
- **TokenBucketRateLimiter**: Token bucket algorithm implementation
  - Configurable tokens per second
  - Burst capacity support
  - Per-key rate limiting
  - Concurrent request handling

- **NoOpRateLimiter**: No-operation rate limiter for testing

### 🛡️ Error Handling
Comprehensive error types:
- `NetworkError`: Network connectivity issues
- `HttpError`: HTTP status code errors with specific codes
- `ParseError`: Response parsing failures
- `RateLimitError`: Rate limit exceeded with retry-after support
- `TimeoutError`: Request timeout errors
- `UnknownError`: Catch-all for unexpected errors

### 🎯 Result Type
Type-safe result wrapper:
- `Result.Success<T>`: Successful operation with data
- `Result.Error`: Failed operation with exception details
- `Result.Loading`: Operation in progress

### 💉 Dependency Injection
Koin-based DI modules:
- **NetworkModule**: Retrofit, OkHttp, and API service configuration
- **DataSourceModule**: Data sources, parsers, and utilities
- **AppModule**: Central module aggregation

## Project Structure

```
src/
├── main/
│   ├── kotlin/com/remotedata/
│   │   ├── data/
│   │   │   ├── mapper/          # DTO to domain mappers
│   │   │   └── remote/
│   │   │       ├── api/         # Retrofit API interfaces
│   │   │       ├── datasource/  # Remote data source implementations
│   │   │       ├── dto/         # Data transfer objects
│   │   │       └── parser/      # HTML parsing utilities
│   │   ├── di/                  # Dependency injection modules
│   │   ├── domain/
│   │   │   └── entity/          # Domain entities
│   │   └── utils/               # Utilities (Result, Exceptions, RateLimiter)
│   └── resources/
│       └── logback.xml          # Logging configuration
└── test/
    ├── kotlin/com/remotedata/
    │   ├── data/remote/
    │   │   ├── datasource/      # Data source unit tests
    │   │   └── parser/          # Parser unit tests
    │   ├── integration/         # Integration tests
    │   └── utils/               # Utility tests
    └── resources/
        └── logback-test.xml     # Test logging configuration
```

## Usage Examples

### Basic API Call

```kotlin
// Initialize Koin
startKoin {
    modules(appModules)
}

// Get data source
val apiDataSource: ApiRemoteDataSource by inject()

// Fetch data
val result = apiDataSource.fetchApiData("3")
when (result) {
    is Result.Success -> println("Data: ${result.data}")
    is Result.Error -> println("Error: ${result.exception.message}")
    is Result.Loading -> println("Loading...")
}
```

### HTML Parsing with Rules

```kotlin
val config = ParsingConfig(
    titleRule = ParsingRule(selector = "h1", extractor = ExtractionType.TEXT),
    contentRule = ParsingRule(selector = "article", extractor = ExtractionType.TEXT),
    linkRules = listOf(
        ParsingRule(selector = "a[href]", attribute = "href", extractor = ExtractionType.ATTR)
    )
)

val parserDataSource: ParserRemoteDataSource by inject()
val result = parserDataSource.parseUrl("https://example.com", config)
```

### Rate Limiting

```kotlin
val rateLimiter = TokenBucketRateLimiter(
    tokensPerSecond = 10.0,
    bucketCapacity = 10
)

rateLimiter.execute("my-api-key") {
    // Your API call here
}
```

## Testing

Run all tests:
```bash
./gradlew test
```

Run with coverage:
```bash
./gradlew koverHtmlReport
```

### Test Coverage

- **Unit Tests**: All core components tested in isolation
- **Integration Tests**: Retrofit with MockWebServer, Koin module loading
- **Parser Tests**: HTML parsing with various configurations
- **Rate Limiter Tests**: Concurrent execution and rate limiting behavior

## API Endpoint

Primary API endpoint: `https://132130.v.nxog.top/api1.php?id=3`

## Dependencies

- Kotlin 1.9.20
- Kotlin Coroutines 1.7.3
- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson 2.10.1
- Jsoup 1.17.1
- Koin 3.5.0
- JUnit 5.10.0
- MockK 1.13.8

## License

Boost Software License - See LICENSE file for details.
