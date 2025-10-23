import React, { useEffect } from 'react';
import { Container, Box, Typography } from '@mui/material';
import { SearchBar } from '../components/SearchBar';
import { SourceFilterChips } from '../components/SourceFilterChips';
import { VideoList } from '../components/VideoList';
import { useSearchViewModel } from '../hooks/useSearchViewModel';
import { SearchViewModel } from '../viewmodels/SearchViewModel';
import { VideoItem } from '../../domain/entities/VideoItem';

interface HomePageProps {
  viewModel: SearchViewModel;
  onVideoSelect: (video: VideoItem) => void;
}

export const HomePage: React.FC<HomePageProps> = ({ viewModel, onVideoSelect }) => {
  const { searchState, sourcesState, search, loadMore, clearSearch, loadSources } =
    useSearchViewModel(viewModel);

  useEffect(() => {
    loadSources();
  }, [loadSources]);

  const handleSearch = (query: string) => {
    if (query.trim().length === 0) {
      clearSearch();
      return;
    }
    search(query, searchState.selectedSourceId || undefined);
  };

  const handleSourceSelect = (sourceId: string | null) => {
    if (searchState.query) {
      search(searchState.query, sourceId || undefined);
    }
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography
          variant="h3"
          component="h1"
          gutterBottom
          sx={{ fontWeight: 700, mb: 3 }}
        >
          Video Search
        </Typography>
        
        <SearchBar
          initialValue={searchState.query}
          onSearch={handleSearch}
          onClear={clearSearch}
        />
      </Box>

      <SourceFilterChips
        sources={sourcesState.sources}
        selectedSourceId={searchState.selectedSourceId}
        onSourceSelect={handleSourceSelect}
        isLoading={sourcesState.isLoading}
        error={sourcesState.error}
      />

      <VideoList
        videos={searchState.videos}
        isLoading={searchState.isLoading}
        error={searchState.error}
        hasMore={searchState.hasMore}
        onLoadMore={loadMore}
        onVideoSelect={onVideoSelect}
        emptyMessage={
          searchState.query
            ? 'No videos found. Try a different search query.'
            : 'Enter a search query to find videos.'
        }
      />
    </Container>
  );
};
