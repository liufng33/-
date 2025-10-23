# Video Parser Application

A video parsing and streaming application built with Clean Architecture principles.

## Overview

This application provides a domain layer for managing video sources, parsing video URLs, searching for videos, and managing playback streams. It follows Clean Architecture to ensure maintainability, testability, and independence from external frameworks.

## Features

- **Video Search**: Search for videos across multiple configured sources
- **Search UI**: Modern React UI with Material Design 3 patterns
- **Source Filtering**: Filter results by video source with interactive chips
- **Video Playback**: HTML5 video player with quality selection
- **Pagination**: Load more results on demand
- **URL Parsing**: Parse pasted video URLs to extract video information
- **Source Management**: Add, edit, delete, and toggle video sources
- **Playback Management**: Fetch and manage playback streams
- **Multi-source Support**: Aggregate results from multiple search sources
- **Parser Configuration**: Configure custom parsers for different video platforms

## Architecture

The project follows Clean Architecture principles with clear separation of concerns:

```
src/
├── domain/                # Business logic layer
│   ├── entities/          # Core business entities
│   ├── value-objects/     # Type-safe value objects
│   ├── repositories/      # Repository interfaces (ports)
│   └── use-cases/         # Business use cases
└── presentation/          # UI layer (React + Material-UI)
    ├── viewmodels/        # Presentation logic
    ├── hooks/             # React hooks
    ├── components/        # Reusable UI components
    ├── pages/             # Full page components
    └── App.tsx            # Main application
```

### Domain Layer

The domain layer contains:
- **Entities**: VideoItem, SourceConfig, ParserConfig, ParseRule, PlaybackLink
- **Value Objects**: SourceId, URL, SourceType, Quality
- **Repository Interfaces**: Contracts for data persistence
- **Use Cases**: Business operations and workflows

For detailed documentation, see [DOMAIN.md](./DOMAIN.md).

### Presentation Layer

The presentation layer provides a complete UI implementation:
- **ViewModels**: SearchViewModel, PlaybackViewModel for managing UI state
- **Components**: SearchBar, VideoCard, VideoList, SourceFilterChips
- **Pages**: HomePage (search and discovery), PlaybackPage (video player)
- **Material Design 3**: Modern, accessible UI with Material-UI
- **Responsive**: Works on mobile, tablet, and desktop

For detailed documentation, see [PRESENTATION.md](./PRESENTATION.md).

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

```bash
npm install
```

### Running Tests

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

### Building

```bash
npm run build
```

### Linting and Formatting

```bash
# Lint code
npm run lint

# Format code
npm run format
```

## Core Concepts

### Entities

**VideoItem**: Represents a video with metadata (title, URL, thumbnail, duration, etc.)

**SourceConfig**: Configuration for a search or parser source with API details

**ParserConfig**: Configuration for parsing video URLs with rules and patterns

**ParseRule**: Individual rule for extracting data (regex, CSS selector, XPath, JSON path)

**PlaybackLink**: Playback URL with quality, format, and expiration information

### Use Cases

**SearchVideosUseCase**: Search for videos across sources with filtering and pagination

**AddSourceUseCase**: Add new video sources with validation

**EditSourceUseCase**: Modify existing source configurations

**DeleteSourceUseCase**: Remove source configurations

**ToggleSourceUseCase**: Enable or disable sources

**ListSourcesUseCase**: List sources with optional filtering

**ImportInitialSourcesUseCase**: Batch import sources for initial setup

**ParsePastedUrlUseCase**: Parse pasted URLs to extract video information

**FetchPlaybackStreamsUseCase**: Fetch playback links for videos

## Repository Interfaces

The domain layer defines repository interfaces that must be implemented by infrastructure layers:

- `ISourceConfigRepository`: Manage source configurations
- `ISearchSourceRepository`: Handle video search operations
- `IParserSourceRepository`: Handle URL parsing operations
- `IParserConfigRepository`: Manage parser configurations
- `IPlaybackRepository`: Handle playback link operations

## Testing

The project includes comprehensive unit tests for:

- Domain entities validation
- Use case business logic
- ViewModels and presentation logic
- React components (UI interactions, loading states, error handling)
- Error handling and edge cases

Test coverage reports are available in the `coverage/` directory after running `npm run test:coverage`.

## Development Guidelines

1. **Follow Clean Architecture**: Keep domain logic independent of frameworks
2. **Write Tests**: All use cases and entities should have unit tests
3. **Use Value Objects**: Wrap primitives in value objects for type safety
4. **Validate Inputs**: Entities and use cases should validate their inputs
5. **Handle Errors**: Use meaningful error messages
6. **Document Code**: Add JSDoc comments for public APIs

## License

This project is licensed under the Boost Software License 1.0 - see the [LICENSE](LICENSE) file for details.

## Future Work

- Implement infrastructure layer (databases, HTTP clients)
- Add URL routing (React Router)
- Implement authentication and authorization
- Add caching layer
- Implement rate limiting
- Add monitoring and logging
- Dark mode support
- Advanced filtering and sorting
