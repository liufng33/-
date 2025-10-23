# Domain Layer Documentation

## Overview

This document describes the domain layer of the video parser application, following Clean Architecture principles. The domain layer contains the core business logic, entities, and use cases, and is independent of external frameworks and infrastructure.

## Architecture

The domain layer is organized into the following components:

```
src/domain/
├── entities/           # Core business entities
├── value-objects/      # Value objects for type safety
├── repositories/       # Repository interfaces (contracts)
└── use-cases/         # Application use cases
```

## Entities

### VideoItem

Represents a video item in the system.

**Properties:**
- `id`: Unique identifier for the video
- `title`: Video title
- `url`: Video page URL
- `thumbnailUrl`: Optional thumbnail URL
- `duration`: Optional video duration in seconds
- `description`: Optional video description
- `sourceId`: ID of the source that provided this video
- `publishDate`: Optional publish date
- `metadata`: Optional additional metadata

**Validation:**
- ID, title, and sourceId cannot be empty
- Duration cannot be negative

### SourceConfig

Represents a search or parser source configuration.

**Properties:**
- `id`: Unique source identifier (SourceId value object)
- `name`: Human-readable source name
- `type`: Source type (SEARCH or PARSER)
- `apiUrl`: API endpoint URL
- `apiKey`: Optional API authentication key
- `enabled`: Whether the source is active
- `priority`: Source priority for ordering (default: 0)
- `rateLimit`: Optional rate limit in requests per minute
- `metadata`: Optional additional metadata

**Methods:**
- `enable()`, `disable()`: Toggle source availability
- `setName()`, `setApiUrl()`, `setApiKey()`: Update properties
- `setPriority()`: Update source priority
- `isSearchSource()`, `isParserSource()`: Type checking

### ParserConfig

Represents a parser configuration for extracting video information from URLs.

**Properties:**
- `id`: Unique parser identifier
- `name`: Parser name
- `urlPattern`: Regular expression pattern to match URLs
- `baseUrl`: Optional base URL for the parser
- `rules`: Array of ParseRule objects
- `headers`: Optional HTTP headers
- `timeout`: Request timeout in milliseconds (default: 30000)
- `enabled`: Whether the parser is active

**Methods:**
- `getRules()`: Get all rules
- `getActiveRules()`: Get enabled rules sorted by priority
- `addRule()`, `removeRule()`: Manage rules
- `matchesUrl()`: Check if a URL matches the parser pattern
- `enable()`, `disable()`: Toggle parser availability

### ParseRule

Represents a single parsing rule for extracting data.

**Properties:**
- `id`: Unique rule identifier
- `name`: Rule name
- `type`: Rule type (REGEX, CSS_SELECTOR, XPATH, JSON_PATH)
- `pattern`: The extraction pattern
- `extractField`: Optional field to extract from matched content
- `priority`: Rule priority (default: 0)
- `enabled`: Whether the rule is active

**Methods:**
- `enable()`, `disable()`: Toggle rule availability

### PlaybackLink

Represents a playback link for a video.

**Properties:**
- `id`: Unique link identifier
- `url`: Playback URL
- `quality`: Video quality (Quality value object)
- `format`: Playback format (MP4, HLS, DASH, WEBM, OTHER)
- `videoId`: Associated video ID
- `headers`: Optional HTTP headers required for playback
- `expiresAt`: Optional expiration timestamp
- `requiresAuth`: Whether authentication is required
- `metadata`: Optional additional metadata

**Methods:**
- `isExpired()`: Check if the link has expired
- `requiresAuthentication()`: Check if auth is required

## Value Objects

### SourceId

Represents a source identifier with validation.

### URL

Represents a validated URL.

### SourceType

Represents the type of source (SEARCH or PARSER).

### Quality

Represents video quality with level and optional resolution.

## Repository Interfaces

### ISourceConfigRepository

Manages persistence of source configurations.

**Methods:**
- `findById(id)`: Find a source by ID
- `findAll()`: Get all sources
- `findByType(type)`: Get sources by type
- `findEnabled()`: Get enabled sources only
- `save(source)`: Save a new source
- `update(source)`: Update an existing source
- `delete(id)`: Delete a source
- `exists(id)`: Check if a source exists

### ISearchSourceRepository

Handles video search operations.

**Methods:**
- `search(source, options)`: Search for videos using a source
- `getActiveSearchSources()`: Get active search sources
- `healthCheck(source)`: Check if a source is operational

### IParserSourceRepository

Handles URL parsing operations.

**Methods:**
- `findParserForUrl(url)`: Find a parser that matches a URL
- `getAllParsers()`: Get all parsers
- `getActiveParsers()`: Get active parsers only
- `parseVideoPage(parser, url)`: Parse a video page

### IParserConfigRepository

Manages persistence of parser configurations.

**Methods:**
- `findById(id)`: Find a parser by ID
- `findAll()`: Get all parsers
- `findEnabled()`: Get enabled parsers only
- `findByUrlPattern(url)`: Find parsers matching a URL
- `save(parser)`: Save a new parser
- `update(parser)`: Update an existing parser
- `delete(id)`: Delete a parser
- `exists(id)`: Check if a parser exists

### IPlaybackRepository

Handles playback link operations.

**Methods:**
- `getPlaybackLinks(video)`: Get all playback links for a video
- `getPlaybackLink(videoId, linkId)`: Get a specific playback link
- `refreshPlaybackLink(link)`: Refresh an expired link

## Use Cases

### SearchVideosUseCase

Searches for videos across configured sources.

**Input:**
- `query`: Search query string
- `sourceId`: Optional specific source to search
- `limit`: Max results per page (default: 20)
- `offset`: Pagination offset (default: 0)
- `filters`: Optional search filters

**Output:**
- `items`: Array of VideoItem objects
- `total`: Total number of results
- `hasMore`: Whether more results are available

**Behavior:**
- When sourceId is provided, searches only that source
- When sourceId is omitted, searches all enabled search sources
- Aggregates results from multiple sources
- Deduplicates videos across sources
- Handles source failures gracefully

### ListSourcesUseCase

Lists configured sources.

**Input:**
- `type`: Optional filter by source type
- `enabledOnly`: Optional flag to return only enabled sources

**Output:**
- Array of SourceConfig objects

### AddSourceUseCase

Adds a new source configuration.

**Input:**
- `id`: Source identifier
- `name`: Source name
- `type`: Source type
- `apiUrl`: API endpoint
- `apiKey`: Optional API key
- `enabled`: Optional enabled flag
- `priority`: Optional priority
- `rateLimit`: Optional rate limit
- `metadata`: Optional metadata

**Output:**
- Created SourceConfig object

**Validation:**
- Source ID must be unique
- URL must be valid
- Name cannot be empty
- Priority cannot be negative

### EditSourceUseCase

Updates an existing source configuration.

**Input:**
- `id`: Source identifier
- `name`: Optional new name
- `apiUrl`: Optional new API URL
- `apiKey`: Optional new API key
- `priority`: Optional new priority
- `metadata`: Optional new metadata

**Output:**
- Updated SourceConfig object

**Validation:**
- Source must exist
- Fields follow same validation as AddSourceUseCase

### DeleteSourceUseCase

Deletes a source configuration.

**Input:**
- `id`: Source identifier

**Validation:**
- Source must exist

### ToggleSourceUseCase

Enables or disables a source.

**Input:**
- `id`: Source identifier
- `enabled`: Desired enabled state

**Output:**
- Updated SourceConfig object

### ImportInitialSourcesUseCase

Imports a batch of sources, typically for initial setup.

**Input:**
- `sources`: Array of source definitions
- `overwriteExisting`: Optional flag to overwrite existing sources

**Output:**
- `imported`: Number of successfully imported sources
- `skipped`: Number of skipped sources
- `failed`: Number of failed imports
- `errors`: Array of error details

**Behavior:**
- Validates each source definition
- Skips existing sources unless overwriteExisting is true
- Continues processing even if some sources fail
- Returns detailed results

### ParsePastedUrlUseCase

Parses a pasted video URL to extract video information.

**Input:**
- `url`: Video page URL

**Output:**
- VideoItem object if successful, null if no parser found

**Behavior:**
- Validates URL format
- Finds appropriate parser for the URL
- Checks if parser is enabled
- Extracts video information using parser rules

### FetchPlaybackStreamsUseCase

Fetches playback links for a video.

**Input:**
- `video`: VideoItem object
- `refreshExpired`: Optional flag to refresh expired links

**Output:**
- Array of PlaybackLink objects

**Behavior:**
- Retrieves all playback links for the video
- Optionally refreshes expired links
- Handles refresh failures gracefully

## Domain Rules

1. **Source Management:**
   - Source IDs must be unique
   - Source names cannot be empty
   - Priorities must be non-negative
   - Rate limits must be positive

2. **Video Items:**
   - Must have valid ID, title, and source ID
   - Duration must be non-negative

3. **Parsers:**
   - URL patterns must be valid regex
   - Rules are applied in priority order
   - Only enabled rules are used

4. **Playback Links:**
   - Must be associated with a video
   - Can expire and may need refreshing
   - May require authentication

## Testing

Unit tests are provided for key use cases:

- `SearchVideosUseCase.test.ts`: Tests search functionality, multi-source aggregation, deduplication
- `AddSourceUseCase.test.ts`: Tests source creation and validation
- `ParsePastedUrlUseCase.test.ts`: Tests URL parsing workflow
- `ImportInitialSourcesUseCase.test.ts`: Tests batch import functionality

Run tests with:
```bash
npm test
```

## Extension Points

The domain layer can be extended by:

1. **New Entities:** Add new business entities in `entities/`
2. **New Value Objects:** Add type-safe value objects in `value-objects/`
3. **New Repositories:** Define new repository contracts in `repositories/`
4. **New Use Cases:** Implement new business operations in `use-cases/`

All extensions should follow Clean Architecture principles:
- Entities contain business logic
- Use cases orchestrate business operations
- Repositories abstract persistence
- Dependencies point inward (domain has no external dependencies)
