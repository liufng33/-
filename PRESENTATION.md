# Presentation Layer Documentation

## Overview

The presentation layer provides a complete UI implementation for the video search and discovery experience using React and Material-UI (Material Design 3).

## Architecture

```
src/presentation/
├── viewmodels/          # Business logic layer
│   ├── SearchViewModel.ts
│   └── PlaybackViewModel.ts
├── hooks/               # React hooks for ViewModels
│   ├── useSearchViewModel.ts
│   └── usePlaybackViewModel.ts
├── components/          # Reusable UI components
│   ├── SearchBar.tsx
│   ├── SourceFilterChips.tsx
│   ├── VideoCard.tsx
│   └── VideoList.tsx
├── pages/               # Full page components
│   ├── HomePage.tsx
│   └── PlaybackPage.tsx
└── App.tsx             # Main application component
```

## ViewModels

### SearchViewModel

Manages the search state and operations:

- **State Management**:
  - Videos list with pagination
  - Loading and error states
  - Search query and selected source
  - Available sources

- **Operations**:
  - `search(query, sourceId?)` - Search for videos
  - `loadMore()` - Load next page of results
  - `clearSearch()` - Clear search results
  - `loadSources()` - Load available sources

- **Observer Pattern**: Uses subscription model for state updates

### PlaybackViewModel

Manages playback state and operations:

- **State Management**:
  - Current video
  - Available playback links
  - Selected playback link
  - Loading and error states

- **Operations**:
  - `loadPlaybackLinks(video, refreshExpired?)` - Load playback options
  - `selectPlaybackLink(link)` - Switch quality/format
  - `clearState()` - Reset state

- **Auto-selection**: Automatically selects best quality link (1080p → 720p → 480p...)

## Components

### SearchBar

Material-UI search input with clear functionality:

```tsx
<SearchBar
  initialValue={query}
  onSearch={(query) => handleSearch(query)}
  onClear={() => handleClear()}
  placeholder="Search videos..."
/>
```

**Features**:
- Submit on Enter or click
- Clear button when text is present
- Accessible design

### SourceFilterChips

Filter chips for source selection:

```tsx
<SourceFilterChips
  sources={sources}
  selectedSourceId={selectedSourceId}
  onSourceSelect={(sourceId) => handleSourceSelect(sourceId)}
  isLoading={isLoading}
  error={error}
/>
```

**Features**:
- "All Sources" option
- Visual feedback for selected source
- Loading and error states
- Responsive layout

### VideoCard

Card component for video display:

```tsx
<VideoCard
  video={video}
  onSelect={(video) => handleSelect(video)}
/>
```

**Features**:
- Thumbnail image
- Title and description (truncated)
- Duration and publish date chips
- Hover effects
- Responsive design

### VideoList

Grid layout for video results with pagination:

```tsx
<VideoList
  videos={videos}
  isLoading={isLoading}
  error={error}
  hasMore={hasMore}
  onLoadMore={() => handleLoadMore()}
  onVideoSelect={(video) => handleSelect(video)}
  emptyMessage="No videos found"
/>
```

**Features**:
- Responsive grid (1-4 columns)
- Loading spinner
- Error display
- Empty state
- "Load More" button
- Pagination support

## Pages

### HomePage

Complete search experience:

**Features**:
- Search bar
- Source filter chips
- Paginated video list
- Loading and error states
- Empty state handling

**Flow**:
1. Load available sources on mount
2. User enters search query
3. Results displayed in grid
4. User can filter by source
5. Load more results on demand
6. Select video to navigate to playback

### PlaybackPage

Video playback and details:

**Features**:
- Video player (HTML5)
- Quality selection
- Video metadata display
- Back navigation
- Loading and error states

**Flow**:
1. Load playback links for video
2. Auto-select best quality
3. Display video player
4. Show quality options
5. Display video details

## Routing

Simple state-based routing in App component:

- `selectedVideo === null` → HomePage
- `selectedVideo !== null` → PlaybackPage

## Material Design 3

The UI follows Material Design 3 principles:

- **Elevation**: Cards with shadow on hover
- **Typography**: Clear hierarchy with Material typography
- **Color**: Primary/secondary color scheme
- **Spacing**: Consistent 8px grid
- **Interactive**: Smooth transitions and hover effects
- **Accessibility**: Proper ARIA labels

## Testing

Comprehensive test coverage:

### ViewModel Tests
- State management
- Use case integration
- Error handling
- Pagination

### Component Tests
- Rendering
- User interactions
- Loading states
- Error states
- Empty states

### Running Tests

```bash
# Run all tests
npm test

# Run in watch mode
npm run test:watch

# Run with coverage
npm run test:coverage
```

## Usage Example

```tsx
import { App } from './presentation/App';
import { SearchViewModel } from './presentation/viewmodels/SearchViewModel';
import { PlaybackViewModel } from './presentation/viewmodels/PlaybackViewModel';
import { SearchVideosUseCase } from './domain/use-cases/search-videos/SearchVideosUseCase';
import { ListSourcesUseCase } from './domain/use-cases/manage-sources/ListSourcesUseCase';
import { FetchPlaybackStreamsUseCase } from './domain/use-cases/fetch-playback/FetchPlaybackStreamsUseCase';

// Initialize repositories (infrastructure layer)
const searchRepo = new SearchSourceRepository();
const sourceRepo = new SourceConfigRepository();
const playbackRepo = new PlaybackRepository();

// Initialize use cases
const searchUseCase = new SearchVideosUseCase(searchRepo, sourceRepo);
const listSourcesUseCase = new ListSourcesUseCase(sourceRepo);
const fetchPlaybackUseCase = new FetchPlaybackStreamsUseCase(playbackRepo);

// Initialize ViewModels
const searchViewModel = new SearchViewModel(searchUseCase, listSourcesUseCase);
const playbackViewModel = new PlaybackViewModel(fetchPlaybackUseCase);

// Render App
ReactDOM.render(
  <App
    searchViewModel={searchViewModel}
    playbackViewModel={playbackViewModel}
  />,
  document.getElementById('root')
);
```

## State Management

The presentation layer uses a **ViewModel + Observer pattern** instead of Redux/Context:

**Advantages**:
- No React-specific dependencies in ViewModels
- Easy to test ViewModels independently
- Clear separation of concerns
- Type-safe state updates
- Simple subscription model

**Pattern**:
1. ViewModels manage state
2. Components subscribe via React hooks
3. State updates trigger re-renders
4. ViewModels call use cases
5. Use cases interact with repositories

## Responsive Design

The UI is fully responsive:

- **Mobile**: Single column grid
- **Tablet**: 2-column grid
- **Desktop**: 3-4 column grid
- **Search bar**: Full width on all devices
- **Filter chips**: Wrap on smaller screens

## Accessibility

All components follow accessibility best practices:

- Semantic HTML
- ARIA labels
- Keyboard navigation
- Focus management
- Screen reader support
- High contrast ratios

## Future Enhancements

Potential improvements:

1. **URL Routing**: Add proper URL routing (React Router)
2. **Advanced Filters**: Date range, duration, sort options
3. **Bookmarks**: Save favorite videos
4. **History**: Track viewed videos
5. **Infinite Scroll**: Replace "Load More" button
6. **Video Preview**: Hover preview with thumbnails
7. **Dark Mode**: Theme switching
8. **Keyboard Shortcuts**: Power user features
9. **PWA**: Progressive Web App support
10. **Animations**: Framer Motion transitions
